package br.com.ffroliva.mimecast.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {

    private String message;
    private String status;

}
