package br.com.ffroliva.mimecast.controller;

import io.restassured.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ServerControllerTest {


    @Autowired
    private WebApplicationContext webAppContextSetup;

    @BeforeEach
    void before() {
        RestAssuredMockMvc.webAppContextSetup(webAppContextSetup);
    }

    @AfterEach
    void after() {
        RestAssuredMockMvc.reset();
    }


    @Test
    void testSearchFiles() {
        final List<String> servers = given()
                .get("/servers").as(new TypeRef<List<String>>() {
                });
        Assertions.assertEquals(servers.get(0), "localhost");
    }
}

