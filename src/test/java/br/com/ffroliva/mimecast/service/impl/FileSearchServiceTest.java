package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import br.com.ffroliva.mimecast.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import reactor.core.publisher.Flux;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileSearchServiceTest {

    @Autowired
    private SearchService searchService;
    private String rootPath;

    @BeforeEach
    void setup() throws URISyntaxException {
        rootPath = Paths.get(getClass().getClassLoader().getResource("aaa").toURI()).toString();
    }

    @Test
    void testSearch() {
        SearchRequest sr = SearchRequest.of("localhost", rootPath, "a");
        final Stream<SearchResponse> searchResult = searchService.search(sr);
        Assertions.assertEquals(4, searchResult.count());
    }

}
