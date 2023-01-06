package me.ehp246.aufrest.provider.jackson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.RestBodyDescriptor;
import me.ehp246.aufrest.core.rest.FromJson;
import me.ehp246.aufrest.core.rest.ToJson;

/**
 * Implements internal JSON operations on {@linkplain ObjectMapper}.
 *
 * @author Lei Yang
 *
 */
public final class JsonByJackson implements FromJson, ToJson {
    private final static Logger LOGGER = LogManager.getLogger(JsonByJackson.class);

    private final ObjectMapper objectMapper;

    public JsonByJackson(final ObjectMapper objectMapper) {
        super();
        this.objectMapper = objectMapper;
    }

    @Override
    public String apply(final Object value, final RestBodyDescriptor<?> valueInfo) {
        if (value == null) {
            return null;
        }

        final var type = valueInfo == null ? value.getClass() : valueInfo.type();
        final var view = valueInfo == null ? null : valueInfo.view();

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

    @Override
    public <T> T apply(final String json, final RestBodyDescriptor<T> descriptor) {
        Objects.requireNonNull(descriptor);

        if (json == null || json.isBlank()) {
            return null;
        }

        final var type = Objects.requireNonNull(descriptor.type());
        final var reifying = descriptor.reifying();

        final var reader = Optional.ofNullable(descriptor.view())
                .map(view -> objectMapper.readerWithView(view)).orElseGet(objectMapper::reader);
        try {
            if (reifying == null) {
                return reader.forType(type).readValue(json);
            } else {
                final var typeFactory = objectMapper.getTypeFactory();
                final var types = new ArrayList<Class<?>>(List.of(type));
                types.addAll(List.of(reifying));

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
