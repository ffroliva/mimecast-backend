package br.com.ffroliva.mimecast.validation.rule;

import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor(staticName = "of")
public class ServerValidationRule implements Rule {

    private final String server;
    private final Set<String> servers;

    @Override
    public void run() {
        if (!servers.contains(server)) {
            throw new BusinessException(MessageProperty.INVALID_SERVER.bind(server));
        }
    }

}
