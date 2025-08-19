package me.ehp246.test.embedded.body;

import java.net.http.HttpRequest.BodyPublishers;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.converter.HttpMessageNotWritableException;

import me.ehp246.aufrest.core.rest.ToJson;
import me.ehp246.test.mock.MockBodyHandler;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled:true" })
class BodyTest {
    @Autowired
    private BodyPublisherCase publisherCase;
    @Autowired
    private BodyHandlerCase handlerCase;
    @Autowired
    private BodyAsCase bodyAsCase;
    @Autowired
    private ToJson toJson;

    @Test
    void publisher_01() {
        final var payload = UUID.randomUUID().toString();

        Assertions.assertEquals(payload, publisherCase.post(BodyPublishers.ofString(payload)).get(0),
                "should be app/json");

        Assertions.assertEquals(payload, publisherCase.postAsJson(BodyPublishers.ofString(payload)).get(0));
    }

    @Test
    void publisher_02() throws HttpMessageNotWritableException {
        final var first = UUID.randomUUID().toString();
        final var last = UUID.randomUUID().toString();

        final var returned = publisherCase.postQueryParams(first, last);

        Assertions.assertEquals(first, returned.get(0));
        Assertions.assertEquals(last, returned.get(1));
    }

    @Test
    void handler_01() {
        final var bodyHandler = new MockBodyHandler<Integer>(1);
        final var original = UUID.randomUUID().toString();

        Assertions.assertEquals(1, handlerCase.postNumber(original, bodyHandler),
                "should ignore the actual response body");
        // Somehow the string is not de-quoted by the controller
        Assertions.assertEquals(toJson.toJson(List.of("\"" + original + "\"")), bodyHandler.asReturned());
    }

    @Test
    void handler_02() {
        Assertions.assertEquals(AppConfig.METHOD_HANDLER, handlerCase.postOnMethod(""));
    }

    @Test
    void handler_03() {
        Assertions.assertEquals("interface", handlerCase.postOnInterface(""));
    }

    @Test
    void bodyAs_01() {
        final var dob = Instant.now();
        final var actual = bodyAsCase.post(new Person(null, null, dob));

        Assertions.assertEquals(null, actual.firstName());
        Assertions.assertEquals(null, actual.lastName());
        Assertions.assertEquals(dob, actual.dob());
    }

    @Test
    void bodyAs_02() {
        final var dob = Instant.now();
        final var actual = bodyAsCase
                .post((PersonDob) new Person(UUID.randomUUID().toString(), UUID.randomUUID().toString(), dob));

        Assertions.assertEquals(null, actual.firstName());
        Assertions.assertEquals(null, actual.lastName());
        Assertions.assertEquals(dob, actual.dob());
    }

    @Test
    void bodyAs_03() {
        final var dob = Instant.now();
        final var expected = new Person(UUID.randomUUID().toString(), UUID.randomUUID().toString(), dob);
        final var actual = bodyAsCase.post((PersonName) expected);

        Assertions.assertEquals(expected.firstName(), actual.firstName());
        Assertions.assertEquals(expected.lastName(), actual.lastName());
        Assertions.assertEquals(null, actual.dob());
    }
}
