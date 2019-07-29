package br.com.ffroliva.mimecast.config.properties;

import com.google.common.collect.ImmutableSet;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;


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

    public Set<String> getServersAsSet() {
        return ImmutableSet.copyOf(Arrays
                .stream(this.getServers().split(","))
                .map(String::trim)
                .iterator());
    }
}
