package br.com.ffroliva.mimecast.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

@Slf4j
@ExtendWith(SpringExtension.class)
class ServerControllerTest {

    public static final String SERVER = "http://localhost:8080";

    @Mock
    private ServerController serverController;

    @Test
    void testSearchFiles() throws Exception {
        Mockito.when(serverController.getServers())
                .thenReturn(Arrays.asList(SERVER));
        Assertions.assertEquals(serverController.getServers().get(0), SERVER);
    }
}

