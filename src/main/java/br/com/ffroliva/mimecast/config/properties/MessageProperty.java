package br.com.ffroliva.mimecast.config.properties;

public enum MessageProperty implements IMessageProperty {

    INVALID_PATH("invalid.path"),
    INVALID_SERVER("invalid.server"),
    INTERNAL_SERVER_ERROR("internal.server.error"),
    MESSAGE_EVENT_DESERIALIZATION_ERROR("message.event.deserialization.error");

    private String[] args = {};

    private String key;

    MessageProperty(String key) {
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

