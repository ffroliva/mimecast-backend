package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FileSearchServiceTest {

    @Autowired
    SearchService searchService;
    String rootPath;

    @BeforeEach
    void setup() throws URISyntaxException {
        URI uri = getClass().getClassLoader().getResource("aaa").toURI();
        rootPath = Paths.get(uri).toString();
    }

    @Test
    void testSearch() {
        SearchRequest sr = SearchRequest.of("localhost", rootPath, "a");
        searchService.search(sr);
    }
}
