package me.ehp246.test.embedded.exception.response;

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
import me.ehp246.aufrest.api.exception.BadRequestException;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.exception.ForbiddenException;
import me.ehp246.aufrest.api.exception.GatewayTimeoutException;
import me.ehp246.aufrest.api.exception.InternalServerErrorException;
import me.ehp246.aufrest.api.exception.NotAcceptableException;
import me.ehp246.aufrest.api.exception.NotAllowedException;
import me.ehp246.aufrest.api.exception.NotAuthorizedException;
import me.ehp246.aufrest.api.exception.NotFoundException;
import me.ehp246.aufrest.api.exception.NotSupportedException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.ServerErrorException;
import me.ehp246.aufrest.api.exception.ServiceUnavailableException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=false" })
class ErrorResponseTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ErrorResponseTestCase errorResponseTestCase;

    @Test
    void test300_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> errorResponseTestCase.get(301));

        Assertions.assertEquals(301, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof RedirectionException, "Should be more specific");
    }

    @Test
    void test300_002() throws RedirectionException {
        final var ex = Assertions.assertThrows(RedirectionException.class, () -> errorResponseTestCase.get02(301));

        Assertions.assertEquals(301, ex.statusCode());
    }

    @Test
    void test400_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> errorResponseTestCase.get(401));

        Assertions.assertEquals(401, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ClientErrorException, "Should be more specific");
    }

    @Test
    void test400_002() {
        final var ex = Assertions.assertThrows(ClientErrorException.class, () -> errorResponseTestCase.get02(401));

        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    void test500_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, errorResponseTestCase::get);

        Assertions.assertEquals(500, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ServerErrorException, "Should be more specific");
    }

    @Test
    void test500_002() {
        final var ex = Assertions.assertThrows(ServerErrorException.class, () -> errorResponseTestCase.get02(500));

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test600_001() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> errorResponseTestCase.get(600));

        Assertions.assertEquals(600, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ErrorResponseException, "Should be more specific");
    }

    @Test
    void test600_002() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> errorResponseTestCase.get02(600),
                "Should be wrapped");

        Assertions.assertEquals(600, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof ErrorResponseException, "Should be more specific");
    }

    @Test
    void test_client_300() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> errorResponseTestCase.getClientError(300),
                "Should be wrapped");

        Assertions.assertEquals(300, ex.statusCode());
        Assertions.assertTrue(ex.getCause() instanceof RedirectionException, "Should be more specific");
    }

    @Test
    void test_client_400() {
        final var ex = Assertions.assertThrows(BadRequestException.class, () -> errorResponseTestCase.getClientError(400));

        Assertions.assertEquals(400, ex.statusCode());
    }

    @Test
    void test_client_401() {
        final var ex = Assertions.assertThrows(NotAuthorizedException.class, () -> errorResponseTestCase.getClientError(401));

        Assertions.assertEquals(401, ex.statusCode());
    }

    @Test
    void test_client_403() {
        final var ex = Assertions.assertThrows(ForbiddenException.class, () -> errorResponseTestCase.getClientError(403));

        Assertions.assertEquals(403, ex.statusCode());
    }

    @Test
    void test_client_404() {
        final var ex = Assertions.assertThrows(NotFoundException.class, () -> errorResponseTestCase.getClientError(404));

        Assertions.assertEquals(404, ex.statusCode());
    }

    @Test
    void test_client_405() {
        final var ex = Assertions.assertThrows(NotAllowedException.class, () -> errorResponseTestCase.getClientError(405));

        Assertions.assertEquals(405, ex.statusCode());
    }

    @Test
    void test_client_406() {
        final var ex = Assertions.assertThrows(NotAcceptableException.class, () -> errorResponseTestCase.getClientError(406));

        Assertions.assertEquals(406, ex.statusCode());
    }

    @Test
    void test_client_415() {
        final var ex = Assertions.assertThrows(NotSupportedException.class, () -> errorResponseTestCase.getClientError(415));

        Assertions.assertEquals(415, ex.statusCode());
    }

    @Test
    void test_server_003() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> errorResponseTestCase.getClientError(500),
                "Should be wrapped");

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test_server_004() {
        final var ex = Assertions.assertThrows(UnhandledResponseException.class, () -> errorResponseTestCase.getClientError(600),
                "Should be wrapped");

        Assertions.assertEquals(600, ex.statusCode());
    }

    @Test
    void test_redirect_001() {
        final var ex = Assertions.assertThrows(RedirectionException.class, () -> errorResponseTestCase.getRedirect(300));

        Assertions.assertEquals(300, ex.statusCode());
    }

    @Test
    void test_redirect_002() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> errorResponseTestCase.getRedirect(400));

        Assertions.assertEquals(400, ex.statusCode());
        Assertions.assertEquals(BadRequestException.class, ex.getClass());
    }

    @Test
    void test_redirect_003() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> errorResponseTestCase.getRedirect(599));

        Assertions.assertEquals(599, ex.statusCode());
    }

    @Test
    void test_redirect_004() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> errorResponseTestCase.getRedirect(699));

        Assertions.assertEquals(699, ex.statusCode());
    }

    @Test
    void test_server_01() {
        final var ex = Assertions.assertThrows(BadGatewayException.class, () -> errorResponseTestCase.getServerError(502));

        Assertions.assertEquals(502, ex.statusCode());
    }

    @Test
    void test_server_02() {
        final var ex = Assertions.assertThrows(ServerErrorException.class, () -> errorResponseTestCase.getServerError(504));

        Assertions.assertEquals(GatewayTimeoutException.class, ex.getClass());
        Assertions.assertEquals(504, ex.statusCode());
    }

    @Test
    void test_error_001() {
        final var ex = Assertions.assertThrows(RedirectionException.class, () -> errorResponseTestCase.getError(399));

        Assertions.assertEquals(399, ex.statusCode());
    }

    @Test
    void test_error_002() {
        final var ex = Assertions.assertThrows(ClientErrorException.class, () -> errorResponseTestCase.getError(499));

        Assertions.assertEquals(499, ex.statusCode());
    }

    @Test
    void test_error_003() {
        final var ex = Assertions.assertThrows(ServerErrorException.class, () -> errorResponseTestCase.getError(599));

        Assertions.assertEquals(599, ex.statusCode());
    }

    @Test
    void test_error_500() {
        final var ex = Assertions.assertThrows(InternalServerErrorException.class, () -> errorResponseTestCase.getError(500));

        Assertions.assertEquals(500, ex.statusCode());
    }

    @Test
    void test_error_502() {
        final var ex = Assertions.assertThrows(BadGatewayException.class, () -> errorResponseTestCase.getError(502));

        Assertions.assertEquals(502, ex.statusCode());
    }

    @Test
    void test_error_503() {
        final var ex = Assertions.assertThrows(ServiceUnavailableException.class, () -> errorResponseTestCase.getError(503));

        Assertions.assertEquals(503, ex.statusCode());
    }

    @Test
    void test_error_504() {
        final var ex = Assertions.assertThrows(GatewayTimeoutException.class, () -> errorResponseTestCase.getError(504));

        Assertions.assertEquals(504, ex.statusCode());
    }

    @Test
    void test_error_004() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> errorResponseTestCase.getError(699));

        Assertions.assertEquals(699, ex.statusCode());
    }

    @Test
    void errorrType_01() {
        final var ex = Assertions.assertThrows(ErrorResponseException.class, () -> errorResponseTestCase.getRedirect(400));

        Assertions.assertEquals(400, ex.statusCode());

        Assertions.assertEquals(400, ex.<Integer>body());
    }

    @Test
    void errorType_02() {
        final var now = Instant.now();
        final var ex = Assertions.assertThrows(ErrorResponseException.class,
                () -> errorResponseTestCase.getBody(objectMapper.writeValueAsString(Map.of("now", now))));

        Assertions.assertEquals(LinkedHashMap.class, ex.httpResponse().body().getClass());
    }
}
