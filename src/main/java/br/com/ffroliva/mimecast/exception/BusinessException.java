package br.com.ffroliva.mimecast.exception;

import br.com.ffroliva.mimecast.config.properties.IMessageProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
    public BusinessException(IMessageProperty iMessageProperty) {
        super(iMessageProperty.message());
        log.debug(iMessageProperty.message());
    }
}
