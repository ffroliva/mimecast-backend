package br.com.ffroliva.mimecast.exception;

import br.com.ffroliva.mimecast.payload.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorMessage> handleBusinessException(final BusinessException e) {
        return error(e, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ErrorMessage> error(
            final Exception exception, final HttpStatus httpStatus) {
        final String message =
                Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
        return new ResponseEntity<>(new ErrorMessage(exception.getMessage(), httpStatus.toString()), httpStatus);
    }
}
