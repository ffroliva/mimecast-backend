package br.com.ffroliva.mimecast.service;

import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.payload.SearchRequest;

import java.util.List;

public interface SearchService {

    List<SearchResponse> search(SearchRequest searchRequest);
}
