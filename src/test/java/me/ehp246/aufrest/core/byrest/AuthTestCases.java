package me.ehp246.aufrest.core.byrest;

import java.util.function.Supplier;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * @author Lei Yang
 *
 */
interface AuthTestCases {
    interface Case001 {
        void get();

        void get(@AuthHeader String auth);

        void get(@AuthHeader Supplier<String> authSupplier);

        void getFailure(@AuthHeader String auth1, @AuthHeader String auth2);
    }

    @ByRest(value = "", auth = @Auth(value = { "postman", "password" }, scheme = AuthScheme.BASIC))
    interface Case002 {
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);
    }

    @ByRest(value = "", auth = @Auth(value = "${api.bearer.token}"))
    interface Case003 {
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

    @ByRest(value = "", auth = @Auth(value = { "${postman.username}", "${postman.password}" }, scheme = AuthScheme.BASIC))
    interface Case005 {
        void get();

        // Overwriting annotation
        void get(@AuthHeader String auth);
    }

    // Exception
    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BASIC))
    interface Case007 {
    }

    // Exception
    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEARER))
    interface Case008 {
    }

    // Exception
    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.SIMPLE))
    interface Case009 {
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.NONE))
    interface Case010 {
        // AuthSupplier returns null.
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);
    }

    interface InvocationAuthCase01 {
        @OfMapping(authProvider = "getOnInvocation")
        void getOnInvocation();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEAN, value = "getOnInterface"))
    interface InvocationAuthCase02 {
        void get();

        @OfMapping(authProvider = "getOnMethod")
        void getOnMethod();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.NONE, value = "getOnInterface"))
    interface InvocationAuthCase03 {
        // Should have no Auth
        void get();

        // Should have Auth
        @OfMapping(authProvider = "getOnMethod")
        void getOnMethod();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.SIMPLE, value = "SIMPLE"))
    interface SimpleAuthCase01 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.SIMPLE))
    interface SimpleAuthCase02 {
        void get();
    }

    @ByRest(value = "", auth = @Auth(scheme = AuthScheme.BEAN))
    interface BeanAuthCase05 {
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
}
