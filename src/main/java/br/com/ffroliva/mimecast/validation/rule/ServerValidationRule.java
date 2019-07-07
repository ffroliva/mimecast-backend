package br.com.ffroliva.mimecast.validation.rule;

import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class ServerValidationRule implements Rule {

    private final String server;

    @Override
    public void run() {
        if(!server.equals("localhost")) {
            throw new BusinessException(MessageProperty.INVALID_PATH);
        }
    }

}
