package br.com.ffroliva.mimecast.config.properties;

public enum MessageProperty implements IMessageProperty {

    INVALID_PATH("invalid.path"),
    ACCESS_DENIED("access.denied");

    private String[] args = {};

    private String key;

    private MessageProperty(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public String message() {
        return MessageSource.get().message(key, args);
    }

    public IMessageProperty bind(String... args) {
        this.args = args;
        return this;
    }

}

