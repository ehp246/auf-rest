package me.ehp246.aufrest.api.exception;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.test.mock.MockHttpResponse;
import me.ehp246.test.mock.MockRestRequest;

/**
 * @author Lei Yang
 *
 */
class ErrorResponseExceptionTest {

    @Test
    void error_01() {
        final var response = new MockHttpResponse<String>(300, UUID.randomUUID().toString(), Map.of("1", List.of("2")));
        final var request = Mockito.mock(RestRequest.class);

        final var exception = new ErrorResponseException(request, response);

        Assertions.assertEquals(request, exception.restRequest());
        Assertions.assertEquals(response, exception.httpResponse());
        Assertions.assertEquals(response.statusCode(), exception.statusCode());
        Assertions.assertEquals(response.body(), exception.body());
        Assertions.assertEquals(response.headers(), exception.headers());
        Assertions.assertEquals(response.headers().map().size(), exception.headersMap().size());
        Assertions.assertEquals(response.headers().map().get("1").size(), exception.headerValues("1").size());
        Assertions.assertEquals(response.headers().map().get("1").get(0), exception.headerValue("1"));

        final var def = UUID.randomUUID().toString();
        Assertions.assertEquals(def, exception.headerValue(UUID.randomUUID().toString(), def));
    }

    @Test
    void client_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ClientErrorResponseException(new MockRestRequest(), new MockHttpResponse<>(500)));
    }

    @Test
    void gateway_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BadGatewayException(new MockRestRequest(), new MockHttpResponse<>(500)));
    }

    @Test
    void gateway_02() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new GatewayTimeoutException(new MockRestRequest(), new MockHttpResponse<>(500)));
    }

    @Test
    void internal_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new InternalServerErrorException(new MockRestRequest(), new MockHttpResponse<>(501)));
    }

    @Test
    void redirect_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RedirectionResponseException(new MockRestRequest(), new MockHttpResponse<>(501)));
    }

    @Test
    void server_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ServerErrorResponseException(new MockRestRequest(), new MockHttpResponse<>(401)));
    }

    @Test
    void service_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ServiceUnavailableException(new MockRestRequest(), new MockHttpResponse<>(501)));
    }
}
