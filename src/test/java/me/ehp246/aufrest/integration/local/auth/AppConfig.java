package me.ehp246.aufrest.integration.local.auth;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import me.ehp246.aufrest.api.configuration.EnableByRest;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BasicAuth;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByRest
@Import(Jackson.class)
class AppConfig {
    final NullPointerException ex = new NullPointerException("What happened?");

    @Bean
    @Profile("authProvider")
    public AuthProvider authProvider() {
        final var countRef = new AtomicReference<Integer>(0);
        final var value = new BasicAuth("basicuser", "password").value();
        return req -> {
            // Only allow one call.
            if (req.uri().contains("/auth/basic") && countRef.get() == 0) {
                countRef.getAndUpdate(i -> i + 1);
                return value;
            }
            return null;
        };
    }

    @Bean
    @Profile("authProviderEx")
    public AuthProvider authProviderEx() {
        return req -> {
            throw ex;
        };
    }

    @Bean("basicAuthBean")
    public BasicAuth basicAuth() {
        return new BasicAuth("basicuser", "password");
    }

    @Bean("dynamicAuthBean")
    public BasicAuthHeaderBuilder dynamicAuthBean() {
        return new BasicAuthHeaderBuilder();
    }

    public class BasicAuthHeaderBuilder {
        public String apply(String username, String password) {
            return new BasicAuth(username, password).value();
        }
    }
}
