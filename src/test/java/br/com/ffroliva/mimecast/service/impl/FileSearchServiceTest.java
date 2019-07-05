package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.service.SearchService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FileSearchServiceTest {

    @Autowired
    SearchService searchService;
    URL url;

    @BeforeEach
    void setup(){
        url = getClass().getClassLoader().getResource("aaa");
    }

    @Test
    void testSearch() throws URISyntaxException {
        searchService.search(url.getPath(), "echo");
    }
}
