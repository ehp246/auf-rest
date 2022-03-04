package me.ehp246.aufrest.integration.local.errortype;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig.AuthConfig;
import me.ehp246.aufrest.core.byrest.ByRestFactory;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class ErrorTypeTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ErrorTypeCase.Case01 case01;

    @Autowired
    private ErrorTypeCase.Case02 case02;

    @Autowired
    private ErrorTypeCase.Case03 case03;

    @Autowired
    private ByRestFactory restFactory;

    @Test
    void errorType_01() {
        final var now = Instant.now();
        final var ex = Assertions.assertThrows(ErrorResponseException.class,
                () -> case01.getBody(objectMapper.writeValueAsString(Map.of("now", now))));

        Assertions.assertEquals(ex.httpResponse().headers(), ex.headers());

        Assertions.assertEquals(ex.httpResponse().headers().map(), ex.headersMap());

        Assertions.assertTrue(ex.body() instanceof ErrorType);

        Assertions.assertEquals(now.toString(), ex.body(ErrorType.class).getNow().toString());
    }

    @Test
    void errorType_02() {
        final var now = Instant.now();
        final var ex = Assertions.assertThrows(ErrorResponseException.class,
                () -> restFactory
                        .newInstance(ErrorTypeCase.Case01.class,
                                new ByRestProxyConfig("http://localhost:${local.server.port}/status-code/",
                                        new AuthConfig(), null,
                                        null, null, true, Object.class))
                        .getBody(objectMapper.writeValueAsString(Map.of("now", now))));

        Assertions.assertTrue(ex.httpResponse().body() instanceof Map);

        Assertions.assertEquals(now.toString(), ex.body(Map.class).get("now").toString());
    }

    @Test
    void errorType_03() {
        final var now = Instant.now();
        final var ex = Assertions.assertThrows(ErrorResponseException.class,
                () -> case02.getBody(objectMapper.writeValueAsString(Map.of("now", now))));

        Assertions.assertTrue(ex.httpResponse().body() instanceof Map);

        Assertions.assertEquals(now.toString(), ex.body(Map.class).get("now").toString());
    }

    @Test
    void errorType_04() throws JsonProcessingException {
        final var now = Instant.now();
        final var string = objectMapper.writeValueAsString(Map.of("now", now));
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> case03.getBody(string));

        Assertions.assertEquals(String.class, ex.httpResponse().body().getClass());

        Assertions.assertEquals(string, ex.body(String.class));
    }
}
