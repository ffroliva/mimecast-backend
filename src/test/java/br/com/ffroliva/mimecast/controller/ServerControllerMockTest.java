package br.com.ffroliva.mimecast.controller;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ServerControllerMockTest {

    @Mock
    private ServerController serverController;

    @Test
    public void testSearchFiles() {
        Mockito.when(serverController.getServers()).thenReturn(Lists.list("localhost"));
        List<String> servers = serverController.getServers();
        Assertions.assertEquals(servers.get(0), "localhost");
    }
}

