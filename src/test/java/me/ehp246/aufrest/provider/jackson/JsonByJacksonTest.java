package me.ehp246.aufrest.provider.jackson;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufrest.api.rest.BindingDescriptor;
import me.ehp246.test.TimingExtension;

/**
 * @author Lei Yang
 *
 */
@ExtendWith(TimingExtension.class)
class JsonByJacksonTest {
    public static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).build()
            .setSerializationInclusion(Include.NON_NULL).registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).registerModule(new MrBeanModule())
            .registerModule(new ParameterNamesModule());

    private final JsonByJackson jackson = new JsonByJackson(OBJECT_MAPPER);

    @SuppressWarnings("unchecked")
    @Test
    void list_001() {
        final var from = List.of(Instant.now(), Instant.now(), Instant.now());

        final List<Instant> back = (List<Instant>) jackson.apply(jackson.apply(from),
                new BindingDescriptor(Instants.class));

        back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void list_002() {
        final var from = List.of(Instant.now(), Instant.now(), Instant.now());

        final List<Instant> back = (List<Instant>) jackson.apply(jackson.apply(from),
                new BindingDescriptor(List.class, null, List.of(Instant.class), List.of()));

        back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void list_003() {
        final var from = List.of(List.of(Instant.now()), List.of(Instant.now(), Instant.now()), List.of(Instant.now()));

        final List<List<Instant>> back = (List<List<Instant>>) jackson.apply(jackson.apply(from),
                new BindingDescriptor(List.class, null, List.of(List.class, Instant.class), List.of()));

        final var all = back.stream().flatMap(List::stream).map(value -> {
            Assertions.assertEquals(true, value instanceof Instant);
            return value;
        }).collect(Collectors.toList());

        Assertions.assertEquals(4, all.size());
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    void list_004() {
        final var from = List.of(new Person(Instant.now(), "Jon", "Snow"),
                new Person(Instant.now(), "Eddard", "Starks"));

        final List<Person> back = (List<Person>) jackson.apply(jackson.apply(from),
                new BindingDescriptor(List.class, null, List.of(Person.class), List.of()));

        back.stream().forEach(value -> {
            Assertions.assertEquals(true, value instanceof Person);
            Assertions.assertEquals(true, value.getDob() instanceof Instant);
            Assertions.assertEquals(true, value.getFirstName() instanceof String);
        });
    }

    @Test
    void perf_001() {

    }
}
