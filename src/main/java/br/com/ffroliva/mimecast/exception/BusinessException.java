package br.com.ffroliva.mimecast.exception;

import br.com.ffroliva.mimecast.config.properties.IMessageProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessException extends RuntimeException {
    public BusinessException(IMessageProperty iMessageProperty) {
        super(iMessageProperty.message());
        log.debug(iMessageProperty.message());
    }
}
