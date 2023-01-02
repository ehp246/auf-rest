package me.ehp246.test.local.auth;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.OfAuth;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.exception.ClientErrorResponseException;
import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByRest("http://localhost:${local.server.port}/auth/basic")
    interface DefaultCase001 {
        void get();

        void get(@OfAuth String basic);

        @OfBody(Void.class)
        HttpResponse<Void> getAsResponse();
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "basicuser",
            "password" }, scheme = AuthScheme.BASIC))
    interface BasicCase002 {
        void get();

        void get(@OfAuth String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "basicuser:password", scheme = AuthScheme.BEARER))
    interface BearerCase003 {
        void get();

        void get(@OfAuth String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "Basic YmFzaWN1c2VyOnBhc3N3b3Jk", scheme = AuthScheme.SIMPLE))
    interface SimpleCase004 {
        void get();

        void get(@OfAuth String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "basicuser",
            "password" }, scheme = AuthScheme.BASIC))
    interface MethodAuthCase001 {
        // Default value, should follow the interface
        @OfMapping
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
        void get(@AuthBean.Param String username, @AuthBean.Param String password) throws ClientErrorResponseException;
    }
}
