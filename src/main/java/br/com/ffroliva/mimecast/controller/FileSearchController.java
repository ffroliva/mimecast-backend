package br.com.ffroliva.mimecast.controller;

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
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
