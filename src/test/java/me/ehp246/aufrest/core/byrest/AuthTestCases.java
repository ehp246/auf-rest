package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.AuthHeader;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.rest.AuthScheme;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
interface AuthTestCases {
    // Default scheme
    @ByRest("")
    interface Case001 {
        void get();

        // AuthHeader overwrite
        void get(@AuthHeader String auth);
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

    @ByRest("")
    interface InvocationAuthCase001 {
        void get();

        @OfMapping(authProvider = "getOnInvocation")
        void getOnInvocation();
    }
}
