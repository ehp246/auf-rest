package org.ehp246.aufrest.provider.jackson;

import java.util.Optional;

import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

/**
 * Qualified class name is referenced in the import selector.
 *
 * @author Lei Yang
 *
 */
public class JacksonConfiguration {

	@Bean
	public JsonByJackson jacksonProvider(final Optional<ObjectMapper> optional) {
		return new JsonByJackson(optional.orElseGet(() -> new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.registerModule(new MrBeanModule())));
	}
}
