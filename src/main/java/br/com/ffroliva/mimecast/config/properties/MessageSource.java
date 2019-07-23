package br.com.ffroliva.mimecast.config.properties;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.util.Locale;

public class MessageSource {
    private static final ResourceBundleMessageSource messageSource;

    static {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("properties/messages");
        messageSource.setDefaultEncoding("UTF-8");
    }

    private static MessageSource INSTANCE;

    private MessageSource() {
    }

    public static MessageSource get() {
        if (INSTANCE == null) {
            INSTANCE = new MessageSource();
        }
        return INSTANCE;
    }

    public String message(String key) {
        return messageSource.getMessage(key, null, Locale.getDefault());
    }

    public String message(String key, String... args) {
        return messageSource.getMessage(key, args, Locale.getDefault());
    }
}

