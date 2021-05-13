package me.ehp246.aufrest.integration.local.header;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import me.ehp246.aufrest.api.rest.HeaderContext;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class HeaderTest {
    @Autowired
    private TestCase001 case001;

    @AfterAll
    static void afterAll() {
        HeaderContext.clear();
    }

    @BeforeEach
    void clear() {
        HeaderContext.clear();
    }

    @Test
    void header_001() {
        final var value = UUID.randomUUID().toString();

        Assertions.assertEquals(value, case001.get(value).get("x-req-id").get(0), "Should have request header");
    }

    @Test
    void header_002() {
        final var headers = case001.get();

        Assertions.assertEquals(AppConfig.VALUES.get(0), headers.get(AppConfig.NAMES.get(0)).get(0));
        Assertions.assertEquals(AppConfig.VALUES.get(1), headers.get(AppConfig.NAMES.get(1)).get(0));
        Assertions.assertEquals(AppConfig.VALUES.get(2), headers.get(AppConfig.NAMES.get(1)).get(1));
    }

    @Test
    void header_003() {
        final var value = UUID.randomUUID().toString();

        HeaderContext.set("x-req-id", value);

        var headers = case001.get();
        Assertions.assertEquals(1, headers.get("x-req-id").size());
        Assertions.assertEquals(value, headers.get("x-req-id").get(0));

        /**
         * Should add to the list.
         */
        HeaderContext.add("x-req-id", value);

        headers = case001.get();
        Assertions.assertEquals(2, headers.get("x-req-id").size());
        Assertions.assertEquals(value, headers.get("x-req-id").get(0));
        Assertions.assertEquals(value, headers.get("x-req-id").get(1));
    }

    /**
     * Request should overwrite Context
     */
    @Test
    void header_004() {
        final var value = UUID.randomUUID().toString();

        HeaderContext.set("x-req-id", UUID.randomUUID().toString());

        Assertions.assertEquals(1, case001.get(value).get("x-req-id").size());
        Assertions.assertEquals(value, case001.get(value).get("x-req-id").get(0));
    }

    /**
     * Context should overwrite Provider
     */
    @Test
    void header_005() {
        final var name = AppConfig.NAMES.get(0);

        HeaderContext.set(name, UUID.randomUUID().toString());

        final var headers = case001.get();

        Assertions.assertEquals(1, headers.get(name).size());
        Assertions.assertEquals(HeaderContext.values(name).get(0), headers.get(name).get(0));
    }

    /**
     * Request should overwrite all
     */
    @Test
    void header_006() {
        // Set implicitly by Provider
        final var name = AppConfig.NAMES.get(1);
        final var value = UUID.randomUUID().toString();

        // Set on Context
        HeaderContext.set(name, UUID.randomUUID().toString());

        // Set on Request
        final var values = case001.get(Map.of(name, List.of(value))).get(name);

        Assertions.assertEquals(1, values.size());
        Assertions.assertEquals(value, values.get(0));
    }

    /**
     * Context should propagate to the execution thread and be sent with the
     * request, then cleared from the execution thread before the invocation
     * returns;
     */
    @Disabled
    @Test
    void header_007() throws Exception {
        final var name = UUID.randomUUID().toString();
        HeaderContext.set(name, UUID.randomUUID().toString());

        final var ref = new AtomicReference<Map<String, List<String>>>();

        final var returned = case001.getAsFuture().thenApply(headers -> {
            // The context should be cleared when the invocation is finished.
            ref.set(HeaderContext.map());
            return headers;
        }).get();

        Assertions.assertEquals(0, ref.get().size());
        Assertions.assertEquals(HeaderContext.values(name).get(0), returned.get(name).get(0));
    }

    @Test
    void header_map_001() {
        final var headers = case001.get(Map.of("h1", List.of("1", "2", "3"), "h2", List.of("4")));

        Assertions.assertEquals(3, headers.get("h1").size());
        Assertions.assertEquals("1", headers.get("h1").get(0));
        Assertions.assertEquals("2", headers.get("h1").get(1));
        Assertions.assertEquals("3", headers.get("h1").get(2));
        Assertions.assertEquals(1, headers.get("h2").size());
        Assertions.assertEquals("4", headers.get("h2").get(0));
    }

    @Test
    void header_map_002() {
        final var headers = case001.getWithMap2(Map.of("h1", "1"));

        Assertions.assertEquals(1, headers.get("h1").size());
        Assertions.assertEquals("1", headers.get("h1").get(0));
    }
}
