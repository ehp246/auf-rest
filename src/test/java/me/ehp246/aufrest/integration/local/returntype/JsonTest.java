package me.ehp246.aufrest.integration.local.returntype;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class JsonTest {
    private static Logger LOGGER = LogManager.getLogger();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JsonTestCase001 case001;

    @Test
    void test_001() {
        final var count = (int) (Math.random() * 10);
        List<Instant> instants;
        try {
            instants = case001.get001(count);
        } catch (final Exception e) {
            LOGGER.error(e);
            throw e;
        }

        Assertions.assertEquals(count, instants.size());

        instants.stream().forEach(instant -> Assertions.assertEquals(true, instant instanceof Instant));
    }

    @Test
    void test_002() {
        final var count = (int) (Math.random() * 10);
        final var returned = case001.get002(count);

        Assertions.assertEquals(true, returned instanceof HttpResponse);

        final var body = returned.body();

        Assertions.assertEquals(count, body.size());
        body.stream().forEach(instant -> Assertions.assertEquals(true, instant instanceof Instant));
    }

    @Disabled
    @Test
    void test_003() throws Exception {
        final var count = (int) (Math.random() * 10);
        final var returned = case001.get003(count);

        Assertions.assertEquals(true, returned instanceof CompletableFuture);

        final var response = returned.get();

        final var body = response.body();

        Assertions.assertEquals(count, body.size());
        body.stream().forEach(instant -> Assertions.assertEquals(true, instant instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_004() {
        final var count = (int) (Math.random() * 10);
        final var returned = case001.get004(count);

        Assertions.assertEquals(true, returned instanceof HttpResponse);
        Assertions.assertEquals(true, returned.body() instanceof List);

        final var body = (List<Instant>) returned.body();

        Assertions.assertEquals(count, body.size());
        body.stream().forEach(instant -> Assertions.assertEquals(true, instant instanceof Instant));
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_005() throws JsonMappingException, JsonProcessingException {
        final var count = (int) (Math.random() * 10);
        final var returned = case001.get006(count);

        Assertions.assertEquals(true, returned instanceof String);

        final var list = objectMapper.readValue(returned, List.class);
        Assertions.assertEquals(count, list.size());

        list.stream().forEach(instant -> {
            try {
                objectMapper.readValue("\"" + instant + "\"", Instant.class);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    void test_006() throws JsonMappingException, JsonProcessingException {
        final var count = (int) (Math.random() * 10);
        final var returned = case001.get007(count).body();

        Assertions.assertEquals(true, returned instanceof String);

        final var list = objectMapper.readValue(returned, List.class);
        Assertions.assertEquals(count, list.size());

        list.stream().forEach(instant -> {
            try {
                objectMapper.readValue("\"" + instant + "\"", Instant.class);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void test_007() {
        Assertions.assertEquals(null, case001.getNull());
    }

    @SuppressWarnings("unchecked")
    @Disabled
    @Test
    void test_008() throws Exception {
        final var count = (int) (Math.random() * 10);
        final var returned = case001.get008(count).get().body();

        Assertions.assertEquals(true, returned instanceof String);

        final var list = objectMapper.readValue(returned, List.class);
        Assertions.assertEquals(count, list.size());

        list.stream().forEach(instant -> {
            try {
                objectMapper.readValue("\"" + instant + "\"", Instant.class);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void test_009() {
        Assertions.assertEquals(true, case001.get007().getDob() instanceof Instant);
    }

    @Test
    void test_010() {
        final var persons = case001.get008();

        Assertions.assertEquals(true, persons instanceof List);
        Assertions.assertEquals(1, persons.size());

        persons.stream().forEach(person -> Assertions.assertEquals(true, person.getDob() instanceof Instant));
    }

    @Test
    void test_011() {
        final var response = case001.get011();

        Assertions.assertEquals(true, response instanceof HttpResponse);
        Assertions.assertEquals(true, response.body() instanceof Person);
    }

    @Test
    @Disabled
    void test_012() throws Exception {
        final var future = case001.get010();

        Assertions.assertEquals(true, future instanceof CompletableFuture);
        Assertions.assertEquals(true, future.get() instanceof Person);
    }

    @Disabled
    @Test
    void test_013() throws Exception {
        final var future = case001.get009();

        Assertions.assertEquals(true, future instanceof CompletableFuture);

        final var list = future.get();
        Assertions.assertEquals(true, list instanceof List);

        list.forEach(person -> Assertions.assertEquals(true, person instanceof Person));
    }
}
