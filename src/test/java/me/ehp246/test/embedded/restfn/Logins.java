package me.ehp246.test.embedded.restfn;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestPayload;

/**
 * @author Lei Yang
 *
 */
interface Logins {
    record Login(String username, String password) {
    }

    interface LoginName {
        @JsonView(RestPayload.class)
        String getUsername();

        String getPassword();
    }
}
