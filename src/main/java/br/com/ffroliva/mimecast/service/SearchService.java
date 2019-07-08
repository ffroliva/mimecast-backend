package br.com.ffroliva.mimecast.service;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;

import java.io.IOException;
import java.util.List;

public interface SearchService {

    List<SearchResponse> search(SearchRequest searchRequest);
}
