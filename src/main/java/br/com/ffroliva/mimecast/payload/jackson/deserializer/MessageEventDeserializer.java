package br.com.ffroliva.mimecast.payload.jackson.deserializer;


import br.com.ffroliva.mimecast.config.properties.MessageProperty;
import br.com.ffroliva.mimecast.exception.BusinessException;
import br.com.ffroliva.mimecast.payload.ErrorResponse;
import br.com.ffroliva.mimecast.payload.MessageEvent;
import br.com.ffroliva.mimecast.payload.SearchResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
/**
 * WebClient needs this class to resolve
 * the generic type br.com.ffroliva.mimecast.payload.Data from MessagEvent.
 */
@Slf4j
public class MessageEventDeserializer extends JsonDeserializer<MessageEvent> {
    @Override
    public MessageEvent deserialize(final JsonParser jp, final DeserializationContext ctxt) {
        log.debug("Starting deserialization of MessageEvent.");
        try {
            final ObjectCodec oc = jp.getCodec();
            final JsonNode node = oc.readTree(jp);
            final String type = node.get("type").asText();
            if (type.equals(MessageEvent.SUCCESS)) {
                final String filePath = node.get("data").get("filePath").asText();
                final long count = node.get("data").get("count").asLong();
                final String server = node.get("data").get("server").asText();
                return MessageEvent.success(SearchResponse.of(filePath, count, server));
            } else {
                final String message = node.get("data").get("message").asText();
                final String status = node.get("data").get("status").asText();
                return MessageEvent.error(ErrorResponse.of(message, status));
            }
        } catch (IOException e) {
         throw new BusinessException(MessageProperty.MESSAGE_EVENT_DESERIALIZATION_ERROR);
        } finally {
            log.debug("Deserialization of MessageEvent finished.");
        }
    }
}
