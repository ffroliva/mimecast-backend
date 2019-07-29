package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.ErrorResponse;
import br.com.ffroliva.mimecast.payload.MessageEvent;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.List;

import static br.com.ffroliva.mimecast.payload.MessageEvent.ERROR;
import static br.com.ffroliva.mimecast.payload.MessageEvent.SUCCESS;
import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ExtendWith(SpringExtension.class)
class FileSearchControllerMockTest {

    private static final String proxyServer = "http://localhost:8080";

    @Mock
    FileSearchController fileSearchController;

    @Test
    void testSearchFiles() {

        String rootPath = requireNonNull(getClass().getClassLoader().getResource("aaa")).getPath();
        String searchTerm = "aaa";
        List<String > servers = Arrays.asList(proxyServer);

        Mockito.when(fileSearchController
                .search(rootPath,searchTerm, servers, null))
                .thenReturn(Flux
                        .from(Flux
                                .just(new MessageEvent(SUCCESS, SearchResponse
                                                .of("aaa", 1, proxyServer)))));
        final Flux<MessageEvent> searchResponses = fileSearchController
                .search(rootPath, searchTerm, servers, null);
        Assertions.assertEquals(Long.valueOf(1L), searchResponses.count().block());
        MessageEvent<SearchResponse> messageEvent = searchResponses.blockFirst();
        Assertions.assertNotNull(messageEvent.getData().getFilePath());
        Assertions.assertEquals(1, messageEvent.getData().getCount());
        Assertions.assertEquals(proxyServer, messageEvent.getData().getServer());
        Assertions.assertEquals(MessageEvent.SUCCESS, messageEvent.getType());
    }

    @Test
    void testHandleBusinessException() {
        BusinessException ex = new BusinessException(MessageProperty.INVALID_PATH.bind("aaa"));
        Mockito.when(fileSearchController
                .handleBusinessException(ex))
                .thenReturn(Flux.just(new MessageEvent(ERROR, new ErrorResponse(ex.getMessage(), BAD_REQUEST.toString()))));
        final Flux<MessageEvent> searchResponses = fileSearchController.handleBusinessException(ex);
        Assertions.assertEquals(Long.valueOf(1L), searchResponses.count().block());
        MessageEvent<ErrorResponse> messageEvent = searchResponses.blockFirst();
        Assertions.assertNotNull(messageEvent.getData().getMessage());
        Assertions.assertEquals(MessageEvent.ERROR, messageEvent.getType());
        Assertions.assertEquals(ex.getMessage(), messageEvent.getData().getMessage());
        Assertions.assertEquals(BAD_REQUEST.toString(), messageEvent.getData().getStatus());
    }

    @Test
    void testHandleConnectException() {
        ConnectException ex = new ConnectException("localhost:8080");
        Mockito.when(fileSearchController
                .handleConnectException(ex))
                .thenReturn(Flux.just(
                        new MessageEvent(ERROR, new ErrorResponse(ex.getMessage(), INTERNAL_SERVER_ERROR.toString()))));
        final Flux<MessageEvent> searchResponses = fileSearchController.handleConnectException(ex);
        Assertions.assertEquals(Long.valueOf(1L), searchResponses.count().block());
        MessageEvent<ErrorResponse> messageEvent = searchResponses.blockFirst();
        Assertions.assertNotNull(messageEvent.getData().getMessage());
        Assertions.assertEquals(MessageEvent.ERROR, messageEvent.getType());
        Assertions.assertEquals(ex.getMessage(), messageEvent.getData().getMessage());
        Assertions.assertEquals(INTERNAL_SERVER_ERROR.toString(), messageEvent.getData().getStatus());
    }

}

