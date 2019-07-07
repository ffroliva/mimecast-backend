package br.com.ffroliva.mimecast.validation.rule;


import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.SearchRequest;
import br.com.ffroliva.mimecast.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ValidationTest {

    @Autowired
    SearchService searchService;

    @Test
    void invalidPathException(){
        SearchRequest sr = SearchRequest.of(
                "notlocalhost",
                "aaa",
                "bbb");
        Assertions.assertThrows(BusinessException.class, () -> searchService.search(sr));
    }
}
