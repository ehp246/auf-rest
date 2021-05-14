package me.ehp246.aufrest.integration.local.auth;

import java.net.http.HttpResponse;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.ByRest.Auth.Scheme;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByRest("http://localhost:${local.server.port}/auth/basic")
    interface DefaultCase001 {
        void get();

        void get(@AuthHeader String basic);

        @Reifying(Void.class)
        HttpResponse<Void> getAsResponse();
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "basicuser",
            "password" }, scheme = Scheme.BASIC))
    interface BasicCase002 {
        void get();

        void get(@AuthHeader String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "basicuser:password", scheme = Scheme.BEARER))
    interface BearerCase003 {
        void get();

        void get(@AuthHeader String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = "Basic YmFzaWN1c2VyOnBhc3N3b3Jk", scheme = Scheme.SIMPLE))
    interface SimpleCase004 {
        void get();

        void get(@AuthHeader String basic);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/basic", auth = @Auth(value = { "basicuser",
    "password" }, scheme = Scheme.BASIC))
    interface MethodAuthCase001 {
        // Default value, should follow the interface
        @OfMapping
        void get();

        // Use the bean
        @OfMapping(authProvider = "passThrough")
        void get(String passThrough);

        // Missing bean
        @OfMapping(authProvider = "get004")
        void get004();

        // Overwritten
        @OfMapping(authProvider = "get005")
        void get005(@AuthHeader String basic);
    }
}
