package me.ehp246.aufrest.provider.jackson;

import java.util.ArrayList;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.rest.Receiver;
import me.ehp246.aufrest.api.rest.TextContentProducer;
import me.ehp246.aufrest.core.util.OneUtil;

/**
 * @author Lei Yang
 *
 */
public class JsonByJackson {
	private final static Logger LOGGER = LogManager.getLogger(JsonByJackson.class);

	private final ObjectMapper objectMapper;

	public JsonByJackson(final ObjectMapper objectMapper) {
		super();
		this.objectMapper = objectMapper;
	}

	public <T> String toText(final TextContentProducer.Supplier supplier) {
		final var value = supplier.value();
		if (value == null) {
			return null;
		}
		/*
		 * if (String.class.isAssignableFrom(supplier.type()) &&
		 * AnnotationUtil.hasType(supplier.annotations(), AsIs.class)) { return
		 * value.toString(); }
		 */

		return OneUtil.orThrow(() -> this.objectMapper.writeValueAsString(value));
	}

	public Object fromText(final String json, final Receiver receiver) {
		if (receiver == null || json == null || json.isBlank()) {
			return null;
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
			LOGGER.error("Failed to de-serialize {}", json, e);
			throw new RuntimeException(e);
		}
	}
}
