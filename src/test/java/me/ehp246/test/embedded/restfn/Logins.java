package me.ehp246.test.embedded.restfn;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
interface Logins {
    record Login(String username, String password) {
    }

    interface LoginName {
        @JsonView(RestView.class)
        String getUsername();

        String getPassword();
    }
}
