package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.ErrorResponse;
import br.com.ffroliva.mimecast.payload.MessageEvent;
import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static br.com.ffroliva.mimecast.controller.FileSearchController.FILE;
import static br.com.ffroliva.mimecast.payload.MessageEvent.ERROR;
import static br.com.ffroliva.mimecast.payload.MessageEvent.SUCCESS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

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
        return Flux.fromStream(servers.stream())
                .flatMap(server -> this.searchAt(request,server, rootPath, searchTerm))
                .delayElements(Duration.of(100L, ChronoUnit.MILLIS))
                ;

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
