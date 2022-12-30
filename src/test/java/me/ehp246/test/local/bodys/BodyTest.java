package me.ehp246.test.local.bodys;

import java.io.IOException;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.util.MultiValueMapAdapter;

import me.ehp246.aufrest.core.byrest.ToJson;
import me.ehp246.aufrest.mock.MockResponseBodyHandler;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restLogger.enabled:true" })
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

        Assertions.assertEquals(payload, publisherCase.post(BodyPublishers.ofString(payload)).get(0));
    }

    @Test
    void publisher_02() throws HttpMessageNotWritableException, IOException {
        final var first = UUID.randomUUID().toString();
        final var last = UUID.randomUUID().toString();
        final var converter = new FormHttpMessageConverter();

        final var outputMessage = new MockHttpOutputMessage();
        converter.write(
                new MultiValueMapAdapter<String, String>(Map.of("first", List.of(first), "last", List.of(last))), null,
                outputMessage);

        Assertions.assertEquals(first,
                publisherCase.postQueryParams(BodyPublishers.ofString(outputMessage.getBodyAsString())).get(0));
        Assertions.assertEquals(last,
                publisherCase.postQueryParams(BodyPublishers.ofString(outputMessage.getBodyAsString())).get(1));
    }

    @Test
    void handler_01() {
        final var bodyHandler = new MockResponseBodyHandler<Integer>(1);
        final var original = UUID.randomUUID().toString();

        Assertions.assertEquals(1, handlerCase.postNumber(original, bodyHandler),
                "should ignore the actual response body");
        // Somehow the string is not de-quoted by the controller
        Assertions.assertEquals(toJson.apply(List.of("\"" + original + "\"")), bodyHandler.asReturned());
    }

    @Test
    void handler_02() {
        Assertions.assertEquals("method", handlerCase.postOnMethod(""));
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
