package me.ehp246.test.embedded.jackson;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
class Logins {
    record Login(String username, String password) {
    }

    record Login1(@JsonView(RestView.class) String username, @JsonView(Logins.class) String password) {
    }

    public interface Login2 {
        public String getUsername();

        @JsonView(RestView.class)
        public String getPassword();
    }
}
