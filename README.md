# mimecast-backend
Backend for mimecast app

## Building the app

```ssh
    mvn clean install
```

## Run

```sh
    mvn spring-boot:run
```

## API end-points

- http://localhost:8080/servers
- http://localhost:8080/file/search

## Testing using cURL

```
 curl 'http://localhost:8080/file/search?servers=http://localhost:8080&rootPath=/tmp&searchTerm=aaa'
```

## Swagger

This app was integrated with Swagger of the endpoints could be documented and tested easily. 
There url to access swagger's UI is: 

- http://localhost:8080/swagger-ui.html#

## Considerations about the stream of data:

One of the main problems about streaming of data relates to the the capacity of clients/consumers 
to process the volume of incoming data. In this app, when searching a folder with many files and 
folders deep, if no **backpressure** mechanism is introduced the browser will probably crash. 
In order to release the pressure to the browser a delayed of 100 miliseconds where introduced and 
it allowed the browser to handle the incoming data properly.

## Considerations about the Server Side Event

Server side events have a accepts `GET` methods and expects to to receive `text/stream` media type'.
The frontend needs to know what type of data is coming from the backend. Conventionally messages 
where defined to be of two types `success` or `error`. The following `MessageEvent` class was 
create to wrap data and its types.

```java
@JsonDeserialize(using = MessageEventDeserializer.class)
@RequiredArgsConstructor
@Getter
public class MessageEvent<T extends Data> {

    public static final String SUCCESS = "success";
    public static final String ERROR = "error";

    private final String type;
    private final T data;

    public static final MessageEvent success(Data data) {
        return new MessageEvent<>(SUCCESS, data);
    }

    public static final MessageEvent error(Data data){
        return new MessageEvent<>(ERROR, data);
    }
}
```

- Data typed as `success` will receive `data` class `SearchResponse`.

```java
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SearchResponse implements Serializable, Data {

    private String filePath;
    private long count;
}
```

- Data typed as `error` will receive data of class `ErrorResponse`


```java
@Getter
@AllArgsConstructor
public class ErrorResponse implements Data {

    private String message;
    private String status;

}
```

## Json Deserializer

Since `data` attribute is generic, a deserialier needed to be created so Webflux `WebClient` would know
how to handle incoming data.

```java
/**
 * WebClient needs this class to resolve
 * the generic type br.com.ffroliva.mimecast.payload.Data from MessagEvent.
 */
@Slf4j
public class MessageEventDeserializer extends JsonDeserializer<MessageEvent> {
    @Override
    public MessageEvent deserialize(final JsonParser jp, final DeserializationContext ctxt) {
        log.debug("Starting deserialization of MessageEvent.");
        try {
            final ObjectCodec oc = jp.getCodec();
            final JsonNode node = oc.readTree(jp);
            final String type = node.get("type").asText();
            if (type.equals(MessageEvent.SUCCESS)) {
                final String filePath = node.get("data").get("filePath").asText();
                final long count = node.get("data").get("count").asLong();
                final String server = node.get("data").get("server").asText();
                return MessageEvent.success(SearchResponse.of(filePath, count, server));
            } else {
                final String message = node.get("data").get("message").asText();
                final String status = node.get("data").get("status").asText();
                return MessageEvent.error(ErrorResponse.of(message, status));
            }
        } catch (IOException e) {
         throw new BusinessException(MessageProperty.MESSAGE_EVENT_DESERIALIZATION_ERROR);
        } finally {
            log.debug("Deserialization of MessageEvent finished.");
        }
    }
}
```

## Stringboot Webflux for the data streaming

Spring framework has a streaming API called webflux. 
It was used in the `FileSearchController.java` to produce the expected data flow.

```java
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileSearchController {

    private final SearchService searchService;

    @GetMapping(
            value = "/search", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MessageEvent> search(
            @RequestParam(value = "rootPath") String rootPath,
            @RequestParam(value = "searchTerm") String searchTerm,
            ServerHttpRequest request) {
        return Flux.fromStream(searchService
                .search(SearchRequest.of(request.getURI().getHost(), rootPath, searchTerm)))
                .map(MessageEvent::success)
                .delayElements(Duration.of(100L, ChronoUnit.MILLIS));
    }

    @ExceptionHandler(BusinessException.class)
    public Flux<MessageEvent> handleBusinessException(BusinessException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        return Flux.just(MessageEvent
                .error(new ErrorResponse(ex.getMessage(), BAD_REQUEST.toString())));
    }

}
```

## Multiple servers requirement

One of the requirements of this app is to be able to search at multiple servers simultaneously. 
This capability was achieved using two configuration properties included in 

## Frontend

This app has integration with mimecast-frontend. Please have a look at the `README.md` file for 
further information about this app.

## Developed by 

Flávio Oliva
