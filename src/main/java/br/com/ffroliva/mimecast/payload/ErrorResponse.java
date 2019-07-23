package br.com.ffroliva.mimecast.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse implements Data {

    private String message;
    private String status;

}
