package me.ehp246.aufrest.core.rest;

import java.util.UUID;
import java.util.function.Supplier;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.rest.AuthScheme;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.api.rest.BearerToken;

/**
 * @author Lei Yang
 *
 */
interface AuthTestCases {
    // Default scheme
    @ByRest("")
    interface Case01 {
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);

        void get(@AuthHeader Supplier<Object> authSupplier);
    }

    @ByRest(value = "", auth = @Auth(value = { "postman", "password" }, scheme = AuthScheme.BASIC))
    interface Case02 {
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);
    }

    @ByRest(value = "", auth = @Auth(value = "${api.bearer.token}"))
    interface Case03 {
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);
    }

    @ByRest(value = "", auth = @Auth(value = "CustomKey custom.header.123", scheme = AuthScheme.SIMPLE))
    interface Case004 {
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);
    }

    @ByRest(value = "", auth = @Auth(value = { "${postman.username}",
            "${postman.password}" }, scheme = AuthScheme.BASIC))
    interface Case05 {
        void get();

        // Overwriting annotation
        void get(@AuthHeader String auth);
    }

    // Exception
    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BASIC))
    interface Case07 {
        void get();
    }

    // Exception
    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEARER))
    interface Case08 {
        void get();
    }

    // Exception
    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.SIMPLE))
    interface Case09 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.NONE))
    interface Case10 {
        // AuthSupplier returns null.
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEAN, value = { "getOnInterface", "basic" }))
    interface BeanAuth01 {
        void get();

        void get(@AuthHeader String header);

        void getOnArgs(@AuthBean.Param String username, @AuthBean.Param String password);
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEAN))
    interface BeanAuth02 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEAN, value = { "getOnInterface", "bearerToken" }))
    interface BeanAuth03 {
        void get(@AuthBean.Param String token);
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEAN, value = { "getOnInterface", "uuid" }))
    interface BeanAuth04 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEAN, value = { "getOnInterface", "header" }))
    interface BeanAuth05 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.NONE))
    interface NoneAuth01 {
        // Should have no Auth
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.SIMPLE, value = "SIMPLE"))
    interface SimpleAuthCase01 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.SIMPLE))
    interface SimpleAuthCase02 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BASIC))
    interface BasicAuthCase01 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BASIC, value = { "user", "name" }))
    interface BasicAuthCase02 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEARER))
    interface BearerAuthCase01 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEARER, value = "token"))
    interface BearerAuthCase02 {
        void get();
    }

    class MockAuthBean {
        private int basiCount = 0;
        private int bearerTokenCount = 0;
        private int randomCount = 0;

        @AuthBean.Invoking
        public String basic(String username, String password) {
            basiCount++;
            return new BasicAuth(username, password).header();
        }

        @AuthBean.Invoking
        public String bearerToken(String token) {
            bearerTokenCount++;
            return new BearerToken(token).header();
        }

        @AuthBean.Invoking("uuid")
        public String random() {
            randomCount++;
            return UUID.randomUUID().toString();
        }

        int takeBasicCount() {
            final var now = basiCount;
            basiCount = 0;
            return now;
        }

        int takeBearerTokenCount() {
            final var now = bearerTokenCount;
            bearerTokenCount = 0;
            return now;
        }

        int takeRandomCount() {
            final var now = randomCount;
            randomCount = 0;
            return now;
        }
    }
}
