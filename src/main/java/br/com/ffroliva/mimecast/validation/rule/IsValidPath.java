package br.com.ffroliva.mimecast.validation.rule;

import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class IsValidPath implements Rule {

    private final File file;

    public static IsValidPath of(File file) {
        return new IsValidPath(file);
    }

    @Override
    public void run() {
        if(!file.exists()){
           throw new BusinessException(MessageProperty.INVALID_PATH);
        }
    }
}
