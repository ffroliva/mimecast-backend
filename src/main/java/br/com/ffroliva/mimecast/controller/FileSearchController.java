package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/file")
public class FileSearchController {

    private final SearchService searchService;
    private String host;

    @GetMapping(
            value = "/search")
    public Flux<SearchResponse> search(
            @RequestParam(value = "rootPath") String rootPath,
            @RequestParam(value = "searchTerm") String searchTerm,
            ServerHttpRequest request) {
        log.debug("Request URI: " + host);
        return Flux.fromStream(searchService
                .search(SearchRequest.of(request.getURI().getHost(), rootPath, searchTerm)));
    }
}
