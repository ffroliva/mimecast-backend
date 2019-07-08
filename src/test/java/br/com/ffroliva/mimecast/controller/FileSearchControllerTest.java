package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import io.restassured.mapper.TypeRef;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
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
import static java.util.Objects.requireNonNull;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FileSearchControllerTest {


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
        String path = requireNonNull(getClass().getClassLoader().getResource("aaa")).getPath();
        log.debug(String.format("path %s", path));
        final MockMvcResponse response = given()
                .body(SearchRequest.of("localhost", path, "search term"))
                .header("Content-Type", "application/json")
                .when()
                .post("/file/search");
        final List<SearchResponse> searchResponses = response
                .as(new TypeRef<List<SearchResponse>>() {
                });
        Assertions.assertTrue(searchResponses.size() > 0);
        SearchResponse searchResponse = searchResponses.get(0);
        Assertions.assertNotNull(searchResponse.getFilePath());
        Assertions.assertEquals(0, searchResponse.getCount());
    }

}

