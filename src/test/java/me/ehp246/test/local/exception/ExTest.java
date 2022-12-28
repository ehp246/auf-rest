package me.ehp246.test.local.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.RedirectionResponseException;
import me.ehp246.aufrest.api.exception.ServerErrorResponseException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class ExTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExCase case001;

    @Autowired
    private ByRestProxyFactory restFactory;

    @Test
    void test300_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> case001.get(301));

        Assertions.assertEquals(301, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof RedirectionResponseException, "Should be more specific");
    }

    @Test
    void test300_002() throws RedirectionResponseException {
        final var ex = Assertions.assertThrows(RedirectionResponseException.class, () -> case001.get02(301));

        Assertions.assertEquals(301, ex.statusCode());
    }

    @Test
    void test400_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> case001.get(401));

        Assertions.assertEquals(401, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ClientErrorResponseException, "Should be more specific");
    }

    @Test
    void test400_002() {
        final var ex = Assertions.assertThrows(ClientErrorResponseException.class, () -> case001.get02(401));

        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    void test500_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, case001::get);

        Assertions.assertEquals(500, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ServerErrorResponseException, "Should be more specific");
    }

    @Test
    void test500_002() {
        final var ex = Assertions.assertThrows(ServerErrorResponseException.class, () -> case001.get02(500));

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test600_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> case001.get(600));

        Assertions.assertEquals(600, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ErrorResponseException, "Should be more specific");
    }

    @Test
    void test600_002() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> case001.get02(600),
                "Should be wrapped");

        Assertions.assertEquals(600, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ErrorResponseException, "Should be more specific");
    }

    @Test
    void test_client_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> case001.getClientError(300),
                "Should be wrapped");

        Assertions.assertEquals(300, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof RedirectionResponseException, "Should be more specific");
    }

    @Test
    void test_client_002() {
        final var ex = Assertions.assertThrows(ClientErrorResponseException.class, () -> case001.getClientError(400));

        Assertions.assertEquals(400, ex.statusCode());
    }

    @Test
    void test_client_003() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> case001.getClientError(500),
                "Should be wrapped");

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test_client_004() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> case001.getClientError(600),
                "Should be wrapped");

        Assertions.assertEquals(600, ex.statusCode());
    }

    @Test
    void test_redirect_001() {
        final var ex = Assertions.assertThrows(RedirectionResponseException.class, () -> case001.getRedirect(300));

        Assertions.assertEquals(300, ex.statusCode());
    }

    @Test
    void test_redirect_002() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> case001.getRedirect(400));

        Assertions.assertEquals(400, ex.statusCode());
        Assertions.assertTrue(ex.getMessage().endsWith("400"), "Should include the body");
    }

    @Test
    void test_redirect_003() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> case001.getRedirect(599));

        Assertions.assertEquals(599, ex.statusCode());
    }

    @Test
    void test_redirect_004() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> case001.getRedirect(699));

        Assertions.assertEquals(699, ex.statusCode());
    }

    @Test
    void test_error_001() {
        final var ex = Assertions.assertThrows(RedirectionResponseException.class, () -> case001.getError(399));

        Assertions.assertEquals(399, ex.statusCode());
    }

    @Test
    void test_error_002() {
        final var ex = Assertions.assertThrows(ClientErrorResponseException.class, () -> case001.getError(499));

        Assertions.assertEquals(499, ex.statusCode());
    }

    @Test
    void test_error_003() {
        final var ex = Assertions.assertThrows(ServerErrorResponseException.class, () -> case001.getError(599));

        Assertions.assertEquals(599, ex.statusCode());
    }

    @Test
    void test_error_004() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> case001.getError(699));

        Assertions.assertEquals(699, ex.statusCode());
    }

    @Test
    void errorrType_01() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> case001.getRedirect(400));

        Assertions.assertEquals(400, ex.statusCode());

        Assertions.assertEquals(400, ex.body(Integer.class));
    }

    @Test
    void errorType_02() {
        final var now = Instant.now();
        final var ex = Assertions.assertThrows(ErrorResponseException.class,
                () -> case001.getBody(objectMapper.writeValueAsString(Map.of("now", now))));

        Assertions.assertEquals(LinkedHashMap.class, ex.httpResponse().body().getClass());
    }
}
