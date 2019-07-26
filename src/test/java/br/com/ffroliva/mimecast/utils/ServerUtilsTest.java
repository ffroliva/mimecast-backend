package br.com.ffroliva.mimecast.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

//@ExtendWith(SpringExtension.class)
@SpringBootTest(
        properties = "spring.main.web-application-type=reactive",
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@TestPropertySource(properties = "server.port=8080")
class ServerUtilsTest {

    @Test
    void test_ping() {
        Assertions.assertTrue(ServerUtils.pingHost("http://localhost:8080"));
        Assertions.assertFalse(ServerUtils.pingHost("http://localhost:9090"));
    }
}
