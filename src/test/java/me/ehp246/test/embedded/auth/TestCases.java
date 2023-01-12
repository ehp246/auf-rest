package me.ehp246.test.embedded.auth;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.OfAuth;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;
import me.ehp246.aufrest.api.annotation.OfResponse.Body;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByRest("http://localhost:${local.server.port}/auth/basic")
    interface Default01 {
        void get();

        void get(@OfAuth String basic);

        @OfResponse(body = @Body(Void.class))
        HttpResponse<Void> getAsResponse();
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "basicuser",
            "password" }, scheme = AuthScheme.BASIC))
    interface Basic02 {
        void get();

        void get(@OfAuth String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "basicuser:password", scheme = AuthScheme.BEARER))
    interface Bearer03 {
        void get();

        void get(@OfAuth String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "Basic YmFzaWN1c2VyOnBhc3N3b3Jk", scheme = AuthScheme.SIMPLE))
    interface Simple04 {
        void get();

        void get(@OfAuth String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "basicuser",
            "password" }, scheme = AuthScheme.BASIC))
    interface MethodAuth01 {
        // Default value, should follow the interface
        @OfRequest
        void get();
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "dynamicAuthBean",
            "basic" }, scheme = AuthScheme.BEAN))
    interface BeanAuth02 {
        void get(@AuthBean.Param String username, @AuthBean.Param String password);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "dynamicAuthBean",
            "wrongName" }, scheme = AuthScheme.BEAN))
    interface BeanAuth03 {
        void get(@AuthBean.Param String username, @AuthBean.Param String password) throws ClientErrorException;
    }
}
