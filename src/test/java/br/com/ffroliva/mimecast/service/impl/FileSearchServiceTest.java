package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.file.Paths;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class FileSearchServiceTest {

    @Autowired
    private SearchService searchService;
    private String rootPath;

    @BeforeEach
    void setup() {
        rootPath = Paths.get(getClass().getClassLoader().getResource("aaa").getPath()).toString();
    }

    @Test
    void testSearch() {
        SearchRequest sr = SearchRequest.of("localhost", rootPath, "a");
        searchService.search(sr);
    }
}
