package br.com.ffroliva.mimecast.validation.rule;

import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class IsValidPath implements Rule {

    private final File file;
    private final String server;

    public static IsValidPath of(File file, String server) {
        return new IsValidPath(file, server);
    }

    @Override
    public void run() {
        if(!file.exists()){
           throw new BusinessException(MessageProperty.INVALID_PATH.bind(file.getAbsolutePath(), server));
        }
    }
}
