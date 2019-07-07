package br.com.ffroliva.mimecast.controller;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController()
public class ServerController {

    @GetMapping("/servers")
    public List<String> getServers() {
        return Lists.newArrayList("localhost");
    }

}
