package me.ehp246.aufrest.provider.jackson;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.core.reflection.ObjectToText;
import me.ehp246.aufrest.core.reflection.TextToObject;
import me.ehp246.aufrest.core.util.AnnotationUtil;

/**
 * @author Lei Yang
 *
 */
public class JsonByJackson {
	private final static Logger LOGGER = LoggerFactory.getLogger(JsonByJackson.class);

	private final ObjectMapper objectMapper;

	public JsonByJackson(final ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	public ObjectToText getToText() {
		return this::toText;
	}

	public TextToObject getFromText() {
		return this::fromText;
	}

	public <T> String toText(final ObjectToText.Supplier<T> supplier) {
		final var value = supplier.get();
		if (value == null) {
			return null;
		}
		if (String.class.isAssignableFrom(supplier.type())
				&& AnnotationUtil.hasType(supplier.annotations(), AsIs.class)) {
			return value.toString();
		}
		try {
			return this.objectMapper.writeValueAsString(value);
		} catch (final Exception e) {
			LOGGER.error("Failed to serialize {}", supplier.get(), e);
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T fromText(final String json, final TextToObject.Receiver<T> receiver) {
		if (receiver == null || json == null || json.isBlank()) {
			return null;
		}

		if (String.class.isAssignableFrom(receiver.type())
				&& AnnotationUtil.hasType(receiver.annotations(), AsIs.class)) {
			return (T) json;
		}

		try {
			final var collectionOf = receiver.annotations() == null ? null
					: receiver.annotations().stream().filter(ann -> ann instanceof CollectionOf).findAny()
							.map(ann -> ((CollectionOf) ann).value()).orElse(null);

			if (collectionOf == null) {
				return objectMapper.readValue(json, receiver.type());
			}
			if (collectionOf.length == 1) {
				return objectMapper.readValue(json,
						objectMapper.getTypeFactory().constructParametricType(receiver.type(), collectionOf));
			} else {
				final var typeFactory = objectMapper.getTypeFactory();
				final var types = new ArrayList<Class<?>>();
				types.add(receiver.type());
				types.addAll(List.of(collectionOf));

				final var size = types.size();
				var type = typeFactory.constructParametricType(types.get(size - 2), types.get(size - 1));
				for (int i = size - 3; i >= 0; i--) {
					type = typeFactory.constructParametricType(types.get(i), type);
				}
				return objectMapper.readValue(json, type);
			}
		} catch (final JsonProcessingException e) {
			LOGGER.error("Failed to de-serialize {}", json, e);
			throw new RuntimeException(e);
		}
	}
}
