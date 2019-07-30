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

This app was integrated with Swagger to document and test the endpoints. 
To access swagger's UI access the following link: 

- http://localhost:8080/swagger-ui.html#

## Webflux as the framework for data streaming

Spring framework was the chosen API to enable streaming of data. 
`FileSearchController.java` contains a method called `search` 
that returns `Flux<MessageEvent>` which enables to consume
produced messages in a non-blocking manner.

```java
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(FILE)
public class FileSearchController {
    public static final String FILE = "/file";
    private static final String SEARCH = "/search";

    private final SearchService searchService;
    private final ApplicationProperties applicationProperties;

    @GetMapping(
            value = SEARCH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MessageEvent> search(
            @RequestParam(value = "rootPath") String rootPath,
            @RequestParam(value = "searchTerm") String searchTerm,
            @RequestParam(value = "servers") List<String> servers,
            ServerHttpRequest request
            ) {
        return Flux.fromStream(servers.parallelStream())
                .flatMap(server -> this.searchAt(request,server, rootPath, searchTerm))
                .delayElements(Duration.of(100L, ChronoUnit.MILLIS));

    }

    private Flux<MessageEvent> searchAt(
            ServerHttpRequest request,
            String server,
            String rootPath,
            String searchTerm
    ) {
        if(server.equals(applicationProperties.getProxyUrl())) {
            // response from proxy server goes here
            return Flux.fromStream(searchService
                    .search(SearchRequest.of(server, rootPath, searchTerm)))
                    .map(MessageEvent::success);
            // response from non-proxy server goes here
        } else if(this.getRequestUrl(request).equals(server)) {
            return Flux.fromStream(searchService
                    .search(SearchRequest.of(server, rootPath, searchTerm)))
                    .map(MessageEvent::success);
        } else {
            // call from a the proxy server to a non-proxy server goes here
            return WebClient.builder().baseUrl(server).build()
                    .get()
                    .uri( uriBuilder -> uriBuilder.path(FILE+SEARCH)
                            .queryParam("servers", server)
                            .queryParam("rootPath", rootPath)
                            .queryParam("searchTerm", searchTerm)
                            .build())
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve().bodyToFlux(MessageEvent.class);
        }
    }

    private String getRequestUrl(ServerHttpRequest request) {
        String requestUrl = null;
        try {
            URL url = new URL(request.getURI().toString());
            requestUrl = url.getProtocol() +"://" + request.getURI().getAuthority();
        } catch (MalformedURLException e) {
            return "";
        }
        return requestUrl;
    }

    @ExceptionHandler(BusinessException.class)
    public Flux<MessageEvent> handleBusinessException(BusinessException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        return Flux.just(MessageEvent.error(new ErrorResponse(ex.getMessage(), BAD_REQUEST.toString())));
    }


    @ExceptionHandler(ConnectException.class)
    public Flux<MessageEvent> handleConnectException(ConnectException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        return Flux.just(MessageEvent.error(new ErrorResponse(
                this.buildCustomErrorMessageFromException(ex),
                INTERNAL_SERVER_ERROR.toString())));
    }

    private String buildCustomErrorMessageFromException(ConnectException ex) {
        int indexOf = ex.getMessage().indexOf("localhost");
        return String.format("Selected server is offline: %s", ex.getMessage().substring(indexOf));
    }

}
```

## Searching at multiple servers

To enable to searching at multiple servers simultaneously two configuration properties were created.

````yaml
app:
  servers: http://localhost:8080, http://localhost:9090
  proxy-url: http://localhost:8080
````

The `/servers` and point reads the `app.servers` property and tries to to ping at each server. 
If ping is successful the server is included in a set of available servers. 

`app.proxy-url` defines the proxy server. All the incoming data passes thru it to be presented to the client.

## Considerations about the stream of data:

One of the main problems about streaming of data relates to the the capacity of clients/consumers 
to process the volume of incoming data being processed. In this app, when searching in a folder with many files and 
folders deep, if no **backpressure** mechanism is introduced the browser will eventually crash. 
In order to release the pressure to the client, a delayed of 100 milliseconds where
introduced and it allowed the browser to handle the incoming data properly.

## Strategy used to consume incoming data

Server side events (SSE) was the strategy used to consume incoming data from the backend. SSE only accepts `GET` methods 
and expects to to receive `text/stream` media type'. 

## Types of incoming messages

The frontend needs to know what type of data is coming from the backend. Conventionally messages 
where defined to be of two types `success` or `error`. `MessageEvent` class was 
create as a wrapper class so we can control what type of data we will be recieving in the frontend

## Managing Deserialization

In this app we can search at various servers simultaneously. By default `http://localhost:8080` acts as a proxy 
server receving messages incoming from all other servers. To delegate request to non-proxy servers we used `WebClient`. 
While using `WebClient` we faced errors while deserializing the `data` attribute from the `MessageEvent` bean. 
The error happened because the `data` attribute is a generic type and the default serializer doesn't know each concrete 
 type to parse at runtime. This error was fix by introducing the `MessageEventDeserializer.class`.    

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

## Erro handling

This app handles two main errors:

1. Non-proxy server is offline. When we search form the `/servers` endpoint is called 
to fetch the set of available servers to search at. If by any chance the server is no longer up but we try to search at 
it `ConnectException` ins thrown by the netty server and handle by the `handleConnectException`.     
2. If the user informs a directory that does no exist 
a `BusinessException` is thrown and handle by the `handleBusinessException`; 
 

## Google guava for the rescue in the file search engine.

While developing the file search engine I faced situations where I was not able to handle exceptions properly. 
I developed two other implementations of the file search, one using `Files.walk` and `Files.walkfiletree`. 

1. At first I used `Files.walk` who eventually might 
throws `java.io.UncheckedIOException: java.nio.file.AccessDeniedException:` 
which can't be catched by a `try-catch` block. I, therefore, dropped this 
implementation.

2. Then, I tried using `Files.walkfiletree`. However, in this implementation 
I was not able to properly return a stream out of it. This implementation was also dropped.

3. My third and final implementation used google guava toolbox to search the 
files in directories. With google guava I was able to handle errors properly. 
Here is the code:

```java
@Slf4j
@Service
public class FileSearchService implements SearchService {

    @Override
    public Stream<SearchResponse> search(SearchRequest searchRequest) {
        try {
            File file = Paths.get(searchRequest.getRootPath()).toFile();
            Validation.execute(ServerValidationRule.of(searchRequest.getHost()));
            Validation.execute(IsValidPath.of(file));
            return StreamSupport
                    .stream(Files.fileTraverser()
                            .breadthFirst(file).spliterator(), true)
                    .filter(f -> f.isFile() && f.canRead())
                    .map(f -> this.searchFileContent(f, searchRequest.getSearchTerm()))
                    .sorted(Comparator.comparing(SearchResponse::getFilePath));
        } catch (Exception e) {
            throw new BusinessException(MessageProperty.INVALID_PATH
                    .bind(searchRequest.getRootPath()));
        }
    }

    private SearchResponse searchFileContent(File file, String searchTerm) {
        SearchResponse response;
        try (BufferedReader br = Files.newReader(file, Charset.defaultCharset())) {
            response = SearchResponse.of(
                    file.getAbsolutePath(),
                    countWordsInFile(searchTerm, br.lines()));
        } catch (Exception e) {
            response = SearchResponse.of(
                    file.getAbsolutePath(),
                    0);
        }
        log.debug(response.toString());
        return response;
    }

    private int countWordsInFile(String searchTerm, Stream<String> linesStream) {
        return linesStream
                .parallel()
                .map(line -> countWordsInLine(line, searchTerm))
                .reduce(0, Integer::sum);
    }

    private int countWordsInLine(String line, String searchTerm) {
        Pattern pattern = Pattern.compile(searchTerm.toLowerCase());
        Matcher matcher = pattern.matcher(line.toLowerCase());

        int count = 0;
        int i = 0;
        while (matcher.find(i)) {
            count++;
            i = matcher.start() + 1;
        }
        return count;
    }
}

```

## Testing for the backend

Most of the main functionaries were tested using JUnit5. 
Some functionaries used Mokito and other Restassured.

## Frontend

This app has an integration with mimecast-frontend project. 
Please have a look at its `README.md` file for 
further information about this project.


## Developed by 

Flávio Oliva

Thanks for reading!
