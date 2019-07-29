package br.com.ffroliva.mimecast.controller;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import br.com.ffroliva.mimecast.utils.ServerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ServerController {

    public static final String SERVERS = "/servers";

    private final ApplicationProperties applicationProperties;

    @GetMapping(value = SERVERS)
    public List<String> getServers() {
        return applicationProperties
                .getServersAsSet()
                .stream()
                .filter(ServerUtils::pingHost)
                .collect(Collectors.toList());
    }

}
