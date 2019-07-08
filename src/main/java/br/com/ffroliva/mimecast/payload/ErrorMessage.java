package br.com.ffroliva.mimecast.payload;

import br.com.ffroliva.mimecast.config.properties.IMessageProperty;
import lombok.Getter;

@Getter
public class ErrorMessage {

    private String error;
    private String message;

    public ErrorMessage(IMessageProperty messageProperty) {
        this.error = messageProperty.key();
        this.message = messageProperty.message();
    }

    public ErrorMessage(String error, String message) {
        this.error = error;
        this.message = message;
    }

}
