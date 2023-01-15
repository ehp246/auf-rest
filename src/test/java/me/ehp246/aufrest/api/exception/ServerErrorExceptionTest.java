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
    void test() {
        // It's an error unless it's within 200.
        Assertions.assertDoesNotThrow(() -> new ErrorResponseException(req, new MockHttpResponse<Object>(0)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BadGatewayException(req, new MockHttpResponse<Object>(532)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BadRequestException(req, new MockHttpResponse<Object>(430)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ClientErrorException(req, new MockHttpResponse<Object>(599)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ForbiddenException(req, new MockHttpResponse<Object>(430)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new GatewayTimeoutException(req, new MockHttpResponse<Object>(530)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new InternalServerErrorException(req, new MockHttpResponse<Object>(530)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new NotAcceptableException(req, new MockHttpResponse<Object>(430)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new NotAllowedException(req, new MockHttpResponse<Object>(430)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new NotAuthorizedException(req, new MockHttpResponse<Object>(430)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new NotFoundException(req, new MockHttpResponse<Object>(430)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new NotSupportedException(req, new MockHttpResponse<Object>(430)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new RedirectionException(req, new MockHttpResponse<Object>(0)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ServerErrorException(req, new MockHttpResponse<Object>(0)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new ServiceUnavailableException(req, new MockHttpResponse<Object>(513)));

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new BadRequestException(req, new MockHttpResponse<Object>(430)));
    }

}
