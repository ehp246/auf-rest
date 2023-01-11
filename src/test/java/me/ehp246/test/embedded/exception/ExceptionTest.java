package me.ehp246.test.embedded.exception;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.exception.BadGatewayException;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.GatewayTimeoutException;
import me.ehp246.aufrest.api.exception.InternalServerErrorException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.ServerErrorException;
import me.ehp246.aufrest.api.exception.ServiceUnavailableException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=true" })
class ExceptionTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestCase testCase;

    @Test
    void test300_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> testCase.get(301));

        Assertions.assertEquals(301, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof RedirectionException, "Should be more specific");
    }

    @Test
    void test300_002() throws RedirectionException {
        final var ex = Assertions.assertThrows(RedirectionException.class, () -> testCase.get02(301));

        Assertions.assertEquals(301, ex.statusCode());
    }

    @Test
    void test400_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> testCase.get(401));

        Assertions.assertEquals(401, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ClientErrorException, "Should be more specific");
    }

    @Test
    void test400_002() {
        final var ex = Assertions.assertThrows(ClientErrorException.class, () -> testCase.get02(401));

        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    void test500_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, testCase::get);

        Assertions.assertEquals(500, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ServerErrorException, "Should be more specific");
    }

    @Test
    void test500_002() {
        final var ex = Assertions.assertThrows(ServerErrorException.class, () -> testCase.get02(500));

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test600_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> testCase.get(600));

        Assertions.assertEquals(600, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ErrorResponseException, "Should be more specific");
    }

    @Test
    void test600_002() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> testCase.get02(600),
                "Should be wrapped");

        Assertions.assertEquals(600, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ErrorResponseException, "Should be more specific");
    }

    @Test
    void test_client_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> testCase.getClientError(300),
                "Should be wrapped");

        Assertions.assertEquals(300, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof RedirectionException, "Should be more specific");
    }

    @Test
    void test_client_002() {
        final var ex = Assertions.assertThrows(ClientErrorException.class, () -> testCase.getClientError(400));

        Assertions.assertEquals(400, ex.statusCode());
    }

    @Test
    void test_client_003() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> testCase.getClientError(500),
                "Should be wrapped");

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test_client_004() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> testCase.getClientError(600),
                "Should be wrapped");

        Assertions.assertEquals(600, ex.statusCode());
    }

    @Test
    void test_redirect_001() {
        final var ex = Assertions.assertThrows(RedirectionException.class, () -> testCase.getRedirect(300));

        Assertions.assertEquals(300, ex.statusCode());
    }

    @Test
    void test_redirect_002() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> testCase.getRedirect(400));

        Assertions.assertEquals(400, ex.statusCode());
        Assertions.assertTrue(ex.getClass() == ClientErrorException.class);
    }

    @Test
    void test_redirect_003() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> testCase.getRedirect(599));

        Assertions.assertEquals(599, ex.statusCode());
    }

    @Test
    void test_redirect_004() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> testCase.getRedirect(699));

        Assertions.assertEquals(699, ex.statusCode());
    }

    @Test
    void test_server_01() {
        final var ex = Assertions.assertThrows(BadGatewayException.class, () -> testCase.getServerError(502));

        Assertions.assertEquals(502, ex.statusCode());
    }

    @Test
    void test_server_02() {
        final var ex = Assertions.assertThrows(ServerErrorException.class, () -> testCase.getServerError(504));

        Assertions.assertEquals(GatewayTimeoutException.class, ex.getClass());
        Assertions.assertEquals(504, ex.statusCode());
    }

    @Test
    void test_error_001() {
        final var ex = Assertions.assertThrows(RedirectionException.class, () -> testCase.getError(399));

        Assertions.assertEquals(399, ex.statusCode());
    }

    @Test
    void test_error_002() {
        final var ex = Assertions.assertThrows(ClientErrorException.class, () -> testCase.getError(499));

        Assertions.assertEquals(499, ex.statusCode());
    }

    @Test
    void test_error_003() {
        final var ex = Assertions.assertThrows(ServerErrorException.class, () -> testCase.getError(599));

        Assertions.assertEquals(599, ex.statusCode());
    }

    @Test
    void test_error_500() {
        final var ex = Assertions.assertThrows(InternalServerErrorException.class, () -> testCase.getError(500));

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test_error_502() {
        final var ex = Assertions.assertThrows(BadGatewayException.class, () -> testCase.getError(502));

        Assertions.assertEquals(502, ex.statusCode());
    }

    @Test
    void test_error_503() {
        final var ex = Assertions.assertThrows(ServiceUnavailableException.class, () -> testCase.getError(503));

        Assertions.assertEquals(503, ex.statusCode());
    }

    @Test
    void test_error_504() {
        final var ex = Assertions.assertThrows(GatewayTimeoutException.class, () -> testCase.getError(504));

        Assertions.assertEquals(504, ex.statusCode());
    }

    @Test
    void test_error_004() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> testCase.getError(699));

        Assertions.assertEquals(699, ex.statusCode());
    }

    @Test
    void errorrType_01() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> testCase.getRedirect(400));

        Assertions.assertEquals(400, ex.statusCode());

        Assertions.assertEquals(400, ex.<Integer>body());
    }

    @Test
    void errorType_02() {
        final var now = Instant.now();
        final var ex = Assertions.assertThrows(ErrorResponseException.class,
                () -> testCase.getBody(objectMapper.writeValueAsString(Map.of("now", now))));

        Assertions.assertEquals(LinkedHashMap.class, ex.httpResponse().body().getClass());
    }
}
