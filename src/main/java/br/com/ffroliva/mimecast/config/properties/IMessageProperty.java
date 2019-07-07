package br.com.ffroliva.mimecast.config.properties;

import java.io.Serializable;

public interface IMessageProperty extends Serializable {

    String key();

    String message();

    IMessageProperty bind(String... args) ;
}

