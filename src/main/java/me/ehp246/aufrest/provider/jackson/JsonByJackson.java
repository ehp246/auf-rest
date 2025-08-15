package me.ehp246.aufrest.provider.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import me.ehp246.aufrest.api.exception.AufRestOpException;
import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.core.rest.FromJson;
import me.ehp246.aufrest.core.rest.ToJson;

/**
 * @author Lei Yang
 * @since 1.0
 */
public final class JsonByJackson implements FromJson, ToJson {
    private final ObjectMapper objectMapper;

    public JsonByJackson(final ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    public String toJson(final Object value, final JacksonTypeDescriptor descriptor) {
        if (value == null) {
            return null;
        }

        ObjectWriter writer = null;
        if (descriptor == null) {
            writer = this.objectMapper.writerFor(value.getClass());
        } else {
            writer = this.objectMapper.writerFor(this.objectMapper.constructType(descriptor.type()));
            if (descriptor.view() != null) {
                writer = writer.withView(descriptor.view());
            }
        }

        try {
            return writer.writeValueAsString(value);
        } catch (final JsonProcessingException e) {
            throw new AufRestOpException(e);
        }
    }

    @Override
    public Object fromJson(final String json, final JacksonTypeDescriptor typeOf) {
        if (json == null || json.isBlank()) {
            return null;
        }

        var reader = this.objectMapper.readerFor(this.objectMapper.constructType(typeOf.type()));
        if (typeOf.view() != null) {
            reader = reader.withView(typeOf.view());
        }

        try {
            return reader.readValue(json);
        } catch (JsonProcessingException e) {
            throw new AufRestOpException(e);
        }
    }
}
