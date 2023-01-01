package me.ehp246.test.local.jackson;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestPayload;

/**
 * @author Lei Yang
 *
 */
class Logins {
    record Login(String username, String password) {
    }

    record Login1(@JsonView(RestPayload.class) String username, @JsonView(Logins.class) String password) {
    }

    public interface Login2 {
        public String getUsername();

        @JsonView(RestPayload.class)
        public String getPassword();
    }
}
