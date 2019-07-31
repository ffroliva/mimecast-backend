package br.com.ffroliva.mimecast.service.impl;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import br.com.ffroliva.mimecast.exception.BusinessException;
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
import reactor.core.publisher.Flux;

import java.net.URISyntaxException;
import java.nio.file.Paths;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.main.web-application-type=reactive")
class FileSearchServiceTest {

    @Autowired
    private SearchService searchService;
    @Autowired
    private ApplicationProperties applicationProperties;
    private String rootPath;

    @BeforeEach
    void setup() throws URISyntaxException {
        rootPath = Paths.get(getClass().getClassLoader().getResource("aaa").toURI()).toString();
    }

    @Test
    void testSearch() {
        SearchRequest sr = SearchRequest.of(applicationProperties.getProxyUrl(), rootPath, "a");
        final Flux<SearchResponse> searchResult = searchService.search(sr);
        Assertions.assertEquals(11L, searchResult.count().block().longValue());
    }


    @Test
    void test_search_at_invalid_path() {
        SearchRequest sr = SearchRequest.of(applicationProperties.getProxyUrl(), "gffdgaa", "a");
        Assertions.assertThrows(BusinessException.class, () -> searchService.search(sr));
    }


}
