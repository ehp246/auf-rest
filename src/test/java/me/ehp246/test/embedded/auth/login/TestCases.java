package me.ehp246.test.embedded.auth.login;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Auth;
import me.ehp246.aufrest.api.annotation.OfAuth;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * @author Lei Yang
 *
 */
interface TestCases {
    @ByRest("http://localhost:${local.server.port}/auth/login/")
    interface Case01 {
        @OfRequest("account/balance")
        int getBalance(@OfAuth String token);
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/login/", name = "loginBean")
    interface Login {
        @OfRequest(value = "token", method = "POST")
        Token getToken(Account account);

        default String getAuthHeader(final Account account) {
            return this.getToken(account).token();
        }
    }

    @ByRest(value = "http://localhost:${local.server.port}/auth/login/", auth = @Auth(value = {
            "loginBean", "getAuthHeader" }, scheme = AuthScheme.BEAN))
    interface Case02 {
        @OfRequest("account/balance")
        int getBalance(@AuthBean.Param Account account);
    }
}