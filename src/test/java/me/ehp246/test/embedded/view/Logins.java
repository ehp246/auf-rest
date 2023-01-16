package me.ehp246.test.embedded.view;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
class Logins {
    record Login(String username, String password) {
    }

    record RequestWithView(@JsonView(RestView.class) String username, @JsonView(Logins.class) String password) {
    }

    public interface ResponseWithView {
        @JsonView(String.class)
        public String getUsername();

        @JsonView(RestView.class)
        public String getPassword();
    }
}
