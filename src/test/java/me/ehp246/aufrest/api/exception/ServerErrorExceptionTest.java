package me.ehp246.aufrest.api.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.test.mock.MockHttpResponse;
import me.ehp246.test.mock.MockRestRequest;

/**
 * @author Lei Yang
 *
 */
class ServerErrorExceptionTest {
    private final RestRequest req = new MockRestRequest();

    @Test
    void test_01() {
        final var response = new MockHttpResponse<Object>(0);
        // It's an error unless it's within 200.
        Assertions.assertDoesNotThrow(() -> new ErrorResponseException(req, response));
    }

    @Test
    void test_02() {
        final var response = new MockHttpResponse<Object>(532);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BadGatewayException(req, response));
    }

    @Test
    void test_03() {
        final var response = new MockHttpResponse<Object>(430);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BadRequestException(req, response));
    }

    @Test
    void test_04() {
        final var response = new MockHttpResponse<Object>(599);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ClientErrorException(req, response));

    }

    @Test
    void test_05() {
        final var response = new MockHttpResponse<Object>(430);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ForbiddenException(req, response));

    }

    @Test
    void test_06() {
        final var response = new MockHttpResponse<Object>(530);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new GatewayTimeoutException(req, response));

    }

    @Test
    void test_07() {
        final var response = new MockHttpResponse<Object>(530);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new InternalServerErrorException(req, response));

    }

    @Test
    void test_08() {
        final var response = new MockHttpResponse<Object>(430);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NotAcceptableException(req, response));

    }

    @Test
    void test_09() {
        final var response = new MockHttpResponse<Object>(430);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NotAllowedException(req, response));

    }

    @Test
    void test_10() {
        final var response = new MockHttpResponse<Object>(430);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NotAuthorizedException(req, response));

    }

    @Test
    void test_11() {
        final var response = new MockHttpResponse<Object>(430);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NotFoundException(req, response));

    }

    @Test
    void test_12() {
        final var response = new MockHttpResponse<Object>(430);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new NotSupportedException(req, response));

    }

    @Test
    void test_13() {
        final var response = new MockHttpResponse<Object>(0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new RedirectionException(req, response));

    }

    @Test
    void test_14() {
        final var response = new MockHttpResponse<Object>(0);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ServerErrorException(req, response));

    }

    @Test
    void test_15() {
        final var response = new MockHttpResponse<Object>(513);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ServiceUnavailableException(req, response));

    }
}
