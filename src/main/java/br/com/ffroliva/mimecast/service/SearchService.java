package br.com.ffroliva.mimecast.service;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

public interface SearchService {
    Stream<SearchResponse> search(SearchRequest searchRequest);
}
