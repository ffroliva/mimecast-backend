package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import reactor.core.publisher.Flux;

import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Slf4j
@ExtendWith(MockitoExtension.class)
class FileSearchControllerMockTest {

    @Mock
    FileSearchController fileSearchController;

    @Mock
    ServerHttpRequest request;

    @Test
    void testSearchFiles() {
        String server = "localhost";
        String rootPath = requireNonNull(getClass().getClassLoader().getResource("aaa")).getPath();
        String searchTerm = "aaa";
        SearchRequest searchRequest = SearchRequest.of("localhost", rootPath, searchTerm);
        Mockito.when(fileSearchController
                .search(rootPath,searchTerm,request))
                .thenReturn(Flux.fromStream(Stream.of(SearchResponse.of("aaa", 1))));
        final Flux<SearchResponse> searchResponses = fileSearchController.search(rootPath, searchTerm,request);
        Assertions.assertEquals(Long.valueOf(1L), searchResponses.count().block());
        SearchResponse searchResponse = searchResponses.blockFirst();
        Assertions.assertNotNull(searchResponse.getFilePath());
        Assertions.assertEquals(1, searchResponse.getCount());
    }

}

