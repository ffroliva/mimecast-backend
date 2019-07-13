package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

class FileSearchServiceTest {

    private SearchService searchService = new FileSearchService();
    private String rootPath;

    @BeforeEach
    void setup() throws URISyntaxException {
        rootPath = Paths.get(getClass().getClassLoader().getResource("aaa").toURI()).toString();
    }

    @Test
    void testSearch() {
        SearchRequest sr = SearchRequest.of("localhost", rootPath, "a");
        final List<SearchResponse> searchResult = searchService.search(sr);
        Assertions.assertEquals(4, searchResult.size());
    }
}
