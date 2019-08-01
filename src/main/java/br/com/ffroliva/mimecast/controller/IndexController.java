package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class IndexController {

    private final ApplicationProperties applicationProperties;

    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

    @RequestMapping("/")
    public void index(ServerHttpResponse response) {
        if (!StringUtils.isEmpty(this.applicationProperties.getFrontend())) {
            log.info("Index - Redirect to front-end URL");
            response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
            response.getHeaders().setLocation(URI.create(this.applicationProperties.getFrontend()));
        }

    }

}
