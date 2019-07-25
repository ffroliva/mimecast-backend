package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.ErrorResponse;
import br.com.ffroliva.mimecast.payload.MessageEvent;
import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileSearchController {
    private static final String SEARCH = "/search";

    private final SearchService searchService;
    private final ApplicationProperties applicationProperties;

    @GetMapping(
            value = SEARCH, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ParallelFlux<MessageEvent> search(
            @RequestParam(value = "rootPath") String rootPath,
            @RequestParam(value = "searchTerm") String searchTerm,
            @RequestParam(value = "servers") List<String> servers
            ) {
        return Flux.fromStream(servers.stream())
                .flatMap(server -> this.searchAt(server, rootPath, searchTerm))
                .delayElements(Duration.of(100L, ChronoUnit.MILLIS))
                .parallel();

    }

    private Flux<MessageEvent> searchAt(String server, String rootPath, String searchTerm) {
        if(server.equals(applicationProperties.getProxyUrl())) {
            return         Flux.fromStream(searchService
                    .search(SearchRequest.of(server, rootPath, searchTerm)))
                    .map(MessageEvent::success)
                    ;
        } else {
            return WebClient.builder().baseUrl(server).build()
                    .get()
                    .uri(SEARCH)
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .retrieve().bodyToFlux(MessageEvent.class);
        }
    }

    @ExceptionHandler(BusinessException.class)
    public Flux<MessageEvent> handleBusinessException(BusinessException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_EVENT_STREAM);
        return Flux.just(MessageEvent
                .error(new ErrorResponse(ex.getMessage(), BAD_REQUEST.toString())));
    }

}
