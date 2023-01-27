package me.ehp246.aufrest.provider.jackson;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.BodyOf;
import me.ehp246.aufrest.core.rest.AufRestConfiguration;
import me.ehp246.aufrest.core.rest.FromJson;
import me.ehp246.aufrest.core.rest.ToJson;

/**
 * Implements internal JSON operations on {@linkplain ObjectMapper}.
 *
 * @author Lei Yang
 * @see AufRestConfiguration#jsonByObjectMapper(ObjectMapper, ObjectMapper)
 * @version 4.0
 */
public final class JsonByObjectMapper implements FromJson, ToJson {
    private final static Logger LOGGER = LogManager.getLogger(JsonByObjectMapper.class);

    private final ObjectMapper objectMapper;

    public JsonByObjectMapper(final ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    public String apply(final Object value, final BodyOf<?> descriptor) {
        if (value == null) {
            return null;
        }

        final var type = descriptor == null ? value.getClass() : descriptor.first();
        final var view = descriptor == null ? null : descriptor.view();

        try {
            if (view == null) {
                return this.objectMapper.writerFor(type).writeValueAsString(value);
            } else {
                return this.objectMapper.writerFor(type).withView(view).writeValueAsString(value);
            }
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T apply(final String json, BodyOf<T> descriptor) {
        descriptor = descriptor == null ? (BodyOf<T>) new BodyOf<>(Object.class) : descriptor;

        if (json == null || json.isBlank()) {
            return null;
        }

        final var type = Objects.requireNonNull(descriptor.reifying().get(0));
        final var reifying = descriptor.reifying();

        final var reader = Optional.ofNullable(descriptor.view())
                .map(view -> objectMapper.readerWithView(view)).orElseGet(objectMapper::reader);
        try {
            if (reifying.size() == 1) {
                return reader.forType(type).readValue(json);
            } else {
                final var typeFactory = objectMapper.getTypeFactory();
                final var types = new ArrayList<Class<?>>(reifying);

                final var size = types.size();
                var javaType = typeFactory.constructParametricType(types.get(size - 2), types.get(size - 1));
                for (int i = size - 3; i >= 0; i--) {
                    javaType = typeFactory.constructParametricType(types.get(i), javaType);
                }

                return reader.forType(javaType).readValue(json);
            }
        } catch (final JsonProcessingException e) {
            LOGGER.atTrace().withThrowable(e).log("Failed to de-serialize: {}", e::getMessage);

            throw new RuntimeException(e);
        }
    }
}
