package br.com.ffroliva.mimecast.exception;


import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.payload.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    //@ExceptionHandler({IOException.class})
    protected ResponseEntity<Object> handleIOException(IOException ex, WebRequest request) {
        List<ErrorMessage> erros = newArrayList(new ErrorMessage(MessageProperty.INTERNAL_SERVER_ERROR.bind(ex.getMessage())));
        log.debug(ex.getMessage(), ex);
        return this.handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        final List<ErrorMessage> errors = newArrayList(new ErrorMessage(ex.getParameter().getParameterName(), ex.getBindingResult().toString()));
        log.debug(ex.getMessage(), ex);
        return this.handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }
}

