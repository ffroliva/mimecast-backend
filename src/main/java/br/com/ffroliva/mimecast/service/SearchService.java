package br.com.ffroliva.mimecast.service;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import reactor.core.publisher.Flux;

public interface SearchService {
    Flux<SearchResponse> search(SearchRequest searchRequest);
}
