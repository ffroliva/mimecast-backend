package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.ErrorResponse;
import br.com.ffroliva.mimecast.payload.MessageEvent;
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
import reactor.core.publisher.Flux;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

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
        SearchRequest searchRequest = SearchRequest.of(server, rootPath, searchTerm);
        Mockito.when(fileSearchController
                .search(rootPath,searchTerm,request))
                .thenReturn(Flux.just(MessageEvent.success(SearchResponse.of("aaa", 1))));
        final Flux<MessageEvent> searchResponses = fileSearchController.search(rootPath, searchTerm,request);
        Assertions.assertEquals(Long.valueOf(1L), searchResponses.count().block());
        MessageEvent<SearchResponse> messageEvent = searchResponses.blockFirst();
        Assertions.assertNotNull(messageEvent.getData().getFilePath());
        Assertions.assertEquals(1, messageEvent.getData().getCount());
        Assertions.assertEquals(MessageEvent.SUCCESS, messageEvent.getType());
    }

    @Test
    void testHandleException() {
        BusinessException ex = new BusinessException(MessageProperty.INVALID_PATH.bind("aaa"));
        Mockito.when(fileSearchController
                .handleBusinessException(ex))
                .thenReturn(Flux.just(MessageEvent.error(new ErrorResponse(ex.getMessage(), BAD_REQUEST.toString()))));
        final Flux<MessageEvent> searchResponses = fileSearchController.handleBusinessException(ex);
        Assertions.assertEquals(Long.valueOf(1L), searchResponses.count().block());
        MessageEvent<ErrorResponse> messageEvent = searchResponses.blockFirst();
        Assertions.assertNotNull(messageEvent.getData().getMessage());
        Assertions.assertEquals(MessageEvent.ERROR, messageEvent.getType());
        Assertions.assertEquals(ex.getMessage(), messageEvent.getData().getMessage());
        Assertions.assertEquals(BAD_REQUEST.toString(), messageEvent.getData().getStatus());
    }

}

