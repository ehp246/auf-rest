package me.ehp246.aufrest.provider.jackson;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufrest.api.rest.BodyReceiver;

/**
 * @author Lei Yang
 *
 */
public class JsonByJacksonTest {
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().setSerializationInclusion(Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).registerModule(new JavaTimeModule())
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
			.registerModule(new ParameterNamesModule());

	private final JsonByJackson jackson = new JsonByJackson(OBJECT_MAPPER);

	@SuppressWarnings("unchecked")
	@Test
	void list_001() {
		final var from = List.of(Instant.now(), Instant.now(), Instant.now());

		final List<Instant> back = (List<Instant>) jackson.fromJson(jackson.toJson(from), () -> Instants.class);

		back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void list_002() {
		final var from = List.of(Instant.now(), Instant.now(), Instant.now());

		final List<Instant> back = (List<Instant>) jackson.fromJson(jackson.toJson(from),
				new BodyReceiver() {
					@Override
					public Class<List> type() {
						return List.class;
					}

					@Override
					public List<Class<?>> reifying() {
						return List.of(Instant.class);
					}

				});

		back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void list_003() {
		final var from = List.of(List.of(Instant.now()), List.of(Instant.now(), Instant.now()), List.of(Instant.now()));

		final List<List<Instant>> back = (List<List<Instant>>) jackson.fromJson(jackson.toJson(from),
				new BodyReceiver() {
					@Override
					public Class<List> type() {
						return List.class;
					}

					@Override
					public List<Class<?>> reifying() {
						return List.of(List.class, Instant.class);
					}
				});

		final var all = back.stream().flatMap(List::stream).map(value -> {
			Assertions.assertEquals(true, value instanceof Instant);
			return value;
		}).collect(Collectors.toList());

		Assertions.assertEquals(4, all.size());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	void list_004() {
		final var from = List.of(new Person(Instant.now(), "Jon", "Snow"),
				new Person(Instant.now(), "Eddard", "Starks"));

		final List<Person> back = (List<Person>) jackson.fromJson(jackson.toJson(from),
				new BodyReceiver() {
					@Override
					public Class<List> type() {
						return List.class;
					}

					@Override
					public List<Class<?>> reifying() {
						return List.of(Person.class);
					}
				});

		back.stream().forEach(value -> {
			Assertions.assertEquals(true, value instanceof Person);
			Assertions.assertEquals(true, value.getDob() instanceof Instant);
			Assertions.assertEquals(true, value.getFirstName() instanceof String);
		});
	}
}
