package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.payload.SearchResponse;
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

import static java.util.Objects.requireNonNull;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class FileSearchControllerMockTest {

    @Mock
    FileSearchController fileSearchController;

    @Test
    public void testSearchFiles() {
        String server = "localhost";
        String rootPath = requireNonNull(getClass().getClassLoader().getResource("aaa")).getPath();
        String searchTerm = "aaa";
        SearchRequest searchRequest = SearchRequest.of("localhost", rootPath, searchTerm);
        Mockito.when(fileSearchController
                .search(searchRequest))
                .thenReturn(Lists.list(SearchResponse.of("aaa", 1)));
        final List<SearchResponse> searchResponses = fileSearchController.search(searchRequest);
        Assertions.assertEquals(1, searchResponses.size());
        SearchResponse searchResponse = searchResponses.get(0);
        Assertions.assertNotNull(searchResponse.getFilePath());
        Assertions.assertEquals(1, searchResponse.getCount());
    }

}

