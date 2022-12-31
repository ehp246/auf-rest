package me.ehp246.aufrest.provider.jackson;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import me.ehp246.aufrest.api.rest.FromJsonDescriptor;
import me.ehp246.aufrest.api.rest.JsonBodyDescriptor;
import me.ehp246.aufrest.api.spi.RestView;
import me.ehp246.aufrest.core.reflection.ReflectedType;
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

    private static final int PERF_COUNT = 1000_000;
    private static final JsonView jsonView = new JsonView() {

        @Override
        public Class<? extends Annotation> annotationType() {
            return JsonView.class;
        }

        @Override
        public Class<?>[] value() {
            return new Class[] { RestView.class };
        }
    };

    private final JsonByJackson jackson = new JsonByJackson(OBJECT_MAPPER);

    @SuppressWarnings("unchecked")
    @Test
    void list_01() {
        final var from = List.of(Instant.now(), Instant.now(), Instant.now());

        final List<Instant> back = (List<Instant>) jackson.apply(jackson.apply(from),
                new FromJsonDescriptor(Instants.class));

        back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void list_02() {
        final var from = List.of(Instant.now(), Instant.now(), Instant.now());

        final List<Instant> back = (List<Instant>) jackson.apply(jackson.apply(from),
                new FromJsonDescriptor(List.class, null, List.of(Instant.class), List.of()));

        back.stream().forEach(value -> Assertions.assertEquals(true, value instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void list_03() {
        final var from = List.of(List.of(Instant.now()), List.of(Instant.now(), Instant.now()), List.of(Instant.now()));

        final List<List<Instant>> back = (List<List<Instant>>) jackson.apply(jackson.apply(from),
                new FromJsonDescriptor(List.class, null, List.of(List.class, Instant.class), List.of()));

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

        final List<Person> back = (List<Person>) jackson.apply(jackson.apply(from),
                new FromJsonDescriptor(List.class, null, List.of(Person.class), List.of()));

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

        final List<TestCases.Person01> result = (List<TestCases.Person01>) jackson.apply(jackson.apply(from),
                new FromJsonDescriptor(List.class, null, List.of(TestCases.Person01.class), List.of(jsonView)));

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

        final var result = (Set<List<TestCases.Person01>>) jackson.apply(jackson.apply(Set.of(from)),
                new FromJsonDescriptor(Set.class, null,
                        List.of(List.class, TestCases.Person01.class),
                        List.of(jsonView)));

        Assertions.assertEquals(1, result.size());

        final var list = result.stream().findFirst().get();

        IntStream.range(0, from.size()).forEach(i -> {
            Assertions.assertEquals(null, list.get(i).getDob());
            Assertions.assertEquals(from.get(i).getFirstName(), list.get(i).getFirstName());
            Assertions.assertEquals(from.get(i).getLastName(), list.get(i).getLastName());
        });
    }

    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.aufrest.perf", matches = "true")
    void perf_001() {
        final var annotations = new ReflectedType(TestCases.class).findMethod("toJson01", Person.class)
                .map(m -> m.getParameters()[0].getAnnotations()).orElse(null);
        final var value = new Person(Instant.now(), UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final var valueInfo = new JsonBodyDescriptor(Person.class, annotations);

        IntStream.range(0, PERF_COUNT).forEach(i -> {
            jackson.apply(value, valueInfo);
        });
    }
}
