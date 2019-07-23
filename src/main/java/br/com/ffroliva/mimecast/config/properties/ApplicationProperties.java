package br.com.ffroliva.mimecast.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("app")
public class ApplicationProperties {

    private ApplicationProperties() {
    }

    @Component
    public static class Documentation {
        public String title;

        public String description;

        public String version;
    }
}
