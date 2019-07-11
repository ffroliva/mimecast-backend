package br.com.ffroliva.mimecast.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApiError {
    private final List<String> errors;
}
