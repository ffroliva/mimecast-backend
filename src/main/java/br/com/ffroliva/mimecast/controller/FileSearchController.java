package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileSearchController implements ApplicationListener<ApplicationReadyEvent> {

    private final SearchService searchService;
    String hostAddress;

    @GetMapping(
            value = "/search",
            consumes = APPLICATION_JSON_VALUE,
            produces = APPLICATION_JSON_VALUE)
    public Flux<SearchResponse> search(
            @RequestParam(value = "rootPath") String rootPath,
            @RequestParam(value = "searchTerm") String searchTerm) {
        return Flux.fromStream(searchService
                .search(SearchRequest.of(hostAddress,rootPath, searchTerm)));
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error(e.getMessage());

        }
    }
}
