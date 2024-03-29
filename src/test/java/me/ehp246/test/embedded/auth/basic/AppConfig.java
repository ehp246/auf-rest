package me.ehp246.test.embedded.auth.basic;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.BasicAuth;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByRest
class AppConfig {
    final NullPointerException ex = new NullPointerException("What happened?");

    @Bean
    @Profile("authProvider")
    public AuthProvider authProvider() {
        final var countRef = new AtomicReference<Integer>(0);
        final var value = new BasicAuth("basicuser", "password").header();
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
    public AuthHeaderBuilder dynamicAuthBean() {
        return new AuthHeaderBuilder();
    }

    @Bean("throwingAuthBean")
    public ThrowingAuthBean throwingAuthBean() {
        return new ThrowingAuthBean();
    }

    public class AuthHeaderBuilder {
        @AuthBean.Invoking
        public String basic(final String username, final String password) {
            return new BasicAuth(username, password).header();
        }

        @AuthBean.Invoking("wrongName")
        public String basic1(final String username, final String password) {
            return new BasicAuth(username + "1", password).header();
        }
    }
}
