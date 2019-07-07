package br.com.ffroliva.mimecast;

import br.com.ffroliva.mimecast.config.properties.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.reactive.config.EnableWebFlux;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@CrossOrigin
@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableSwagger2
//@EnableWebFlux
@ComponentScan(basePackages = "br.com.ffroliva")
public class MimecastApplication {

	public static void main(String[] args) {
		SpringApplication.run(MimecastApplication.class, args);
	}

}
