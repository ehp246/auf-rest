package me.ehp246.aufrest.integration.local.requestbody;

import java.io.IOException;
import java.net.http.HttpRequest.BodyPublishers;
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

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class BodyTest {
    @Autowired
    private BodyPublisherCase publisherCase;

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
}
