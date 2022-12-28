package me.ehp246.aufrest.provider.jackson;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.rest.BindingDescriptor;
import me.ehp246.aufrest.api.rest.ValueDescriptor;
import me.ehp246.aufrest.api.spi.FromJson;
import me.ehp246.aufrest.api.spi.ToJson;
import me.ehp246.aufrest.core.util.OneUtil;

/**
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
    public String apply(final Object value, final ValueDescriptor valueInfo) {
        if (value == null) {
            return null;
        }

        if (valueInfo != null) {
            return OneUtil.orThrow(() -> {
                final var view = valueInfo.firstJsonViewValue();
                ObjectWriter writer = null;
                if (view != null) {
                    writer = this.objectMapper.writerWithView(view);
                } else {
                    writer = this.objectMapper.writerFor(valueInfo.type());
                }
                return writer.writeValueAsString(value);
            });
        }

        return OneUtil.orThrow(() -> this.objectMapper.writeValueAsString(value));
    }

    @Override
    public Object apply(final String json, final BindingDescriptor receiver) {
        if (receiver == null || json == null || json.isBlank()) {
            return null;
        }

        if (OneUtil.isPresent(receiver.annotations(), AsIs.class)) {
            return json;
        }

        try {
            final var reifying = Optional.ofNullable(receiver.reifying()).orElseGet(ArrayList::new);

            if (reifying.size() == 0) {
                return objectMapper.readValue(json, receiver.type());
            }

            if (reifying.size() == 1) {
                return objectMapper.readValue(json, objectMapper.getTypeFactory()
                        .constructParametricType(receiver.type(), reifying.toArray(new Class<?>[] {})));
            } else {
                final var typeFactory = objectMapper.getTypeFactory();
                final var types = new ArrayList<Class<?>>();
                types.add(receiver.type());
                types.addAll(reifying);

                final var size = types.size();
                var type = typeFactory.constructParametricType(types.get(size - 2), types.get(size - 1));
                for (int i = size - 3; i >= 0; i--) {
                    type = typeFactory.constructParametricType(types.get(i), type);
                }
                return objectMapper.readValue(json, type);
            }
        } catch (final JsonProcessingException e) {
            LOGGER.atTrace().withThrowable(e).log("Failed to de-serialize: {}", e::getMessage);

            throw new RuntimeException(e);
        }
    }
}
