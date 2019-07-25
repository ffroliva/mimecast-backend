package br.com.ffroliva.mimecast.config.properties;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties("app")
@Data
public class ApplicationProperties {

    private String servers;
    private String proxyUrl;

    @Component
    public static class Documentation {
        public String title;

        public String description;

        public String version;
    }
}
