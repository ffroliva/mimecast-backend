package br.com.ffroliva.mimecast.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping
public class ServerController {

    @GetMapping(value = "/servers")
    public List<String> getServers() {
        return Arrays.asList("localhost");
    }

}
