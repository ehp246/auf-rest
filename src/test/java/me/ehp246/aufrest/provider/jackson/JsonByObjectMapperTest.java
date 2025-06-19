package me.ehp246.aufrest.provider.jackson;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.api.rest.ParameterizedTypeBuilder;
import me.ehp246.aufrest.api.spi.RestView;
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
    void list_01() {
        final var from = List.of(Instant.now(), Instant.now(), Instant.now());

        final List<Instant> back = (List<Instant>) jackson.fromJson(jackson.toJson(from),
                new JacksonTypeDescriptor(ParameterizedTypeBuilder.ofList(Instant.class)));

        back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void list_02() {
        final var from = List.of(Instant.now(), Instant.now(), Instant.now());

        final List<Instant> back = (List<Instant>) jackson.fromJson(jackson.toJson(from),
                new JacksonTypeDescriptor(ParameterizedTypeBuilder.ofList(Instant.class)));

        back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void list_03() {
        final var from = List.of(List.of(Instant.now()), List.of(Instant.now(), Instant.now()), List.of(Instant.now()));

        final List<List<Instant>> back = (List<List<Instant>>) jackson.fromJson(jackson.toJson(from),
                new JacksonTypeDescriptor(ParameterizedTypeBuilder.ofList(ParameterizedTypeBuilder.ofList(Instant.class))));

        final var all = back.stream().flatMap(List::stream).map(value -> {
            Assertions.assertEquals(true, value instanceof Instant);
            return value;
        }).collect(Collectors.toList());

        Assertions.assertEquals(4, all.size());
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    void list_04() {
        final var from = List.of(new Person(Instant.now(), "Jon", "Snow"),
                new Person(Instant.now(), "Eddard", "Starks"));

        final List<Person> back = (List<Person>) jackson.fromJson(jackson.toJson(from),
                new JacksonTypeDescriptor(ParameterizedTypeBuilder.ofList(Person.class)));

        back.stream().forEach(value -> {
            Assertions.assertEquals(true, value instanceof Person);
            Assertions.assertEquals(true, value.getDob() instanceof Instant);
            Assertions.assertEquals(true, value.getFirstName() instanceof String);
        });
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    void list_05() {
        final var from = List.of(new Person(Instant.now(), "Jon", "Snow"),
                new Person(Instant.now(), "Eddard", "Starks"));

        final List<TestCases.Person01> result = (List<TestCases.Person01>) jackson.fromJson(jackson.toJson(from),
                new JacksonTypeDescriptor(ParameterizedTypeBuilder.ofList(TestCases.Person01.class), RestView.class));

        IntStream.range(0, from.size()).forEach(i -> {
            Assertions.assertEquals(null, result.get(i).getDob());
            Assertions.assertEquals(from.get(i).getFirstName(), result.get(i).getFirstName());
            Assertions.assertEquals(from.get(i).getLastName(), result.get(i).getLastName());
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    void set_01() {
        final var from = List.of(new Person(Instant.now(), "Jon", "Snow"),
                new Person(Instant.now(), "Eddard", "Starks"));

        final var result = (Set<List<TestCases.Person01>>) jackson.fromJson(jackson.toJson(Set.of(from)),
                new JacksonTypeDescriptor(
                        ParameterizedTypeBuilder.ofSet(ParameterizedTypeBuilder.ofList(TestCases.Person01.class)),
                        RestView.class));

        Assertions.assertEquals(1, result.size());

        final var list = result.stream().findFirst().get();

        IntStream.range(0, from.size()).forEach(i -> {
            Assertions.assertEquals(null, list.get(i).getDob());
            Assertions.assertEquals(from.get(i).getFirstName(), list.get(i).getFirstName());
            Assertions.assertEquals(from.get(i).getLastName(), list.get(i).getLastName());
        });
    }

    // @Test
    void view_01() throws JsonMappingException, JsonProcessingException {
        final var expected = new Person(Instant.now(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                null);

        final var json = OBJECT_MAPPER.writer().forType(Person.class).writeValueAsString(expected);

        Assertions.assertEquals(true, json.contains(expected.getFirstName()));

        var writer = OBJECT_MAPPER.writer().forType(Person.class).withView(RestView.class);

        var jsonView = writer.writeValueAsString(expected);

        Assertions.assertEquals(false, jsonView.contains(expected.getFirstName()));

        writer = writer.withView(String.class);

        jsonView = writer.writeValueAsString(expected);

        Assertions.assertEquals(false, jsonView.contains(expected.getFirstName()));
    }
}
