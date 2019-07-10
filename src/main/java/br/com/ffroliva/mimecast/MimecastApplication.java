package br.com.ffroliva.mimecast;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableWebFlux
@EnableIntegration
public class MimecastApplication {

    public static void main(String[] args) {
        SpringApplication.run(MimecastApplication.class, args);
    }

}
