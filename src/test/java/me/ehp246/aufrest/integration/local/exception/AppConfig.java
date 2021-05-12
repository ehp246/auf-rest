package me.ehp246.aufrest.integration.local.exception;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByRest
@Import(Jackson.class)
class AppConfig {
    @Bean
    public AuthProvider authProvider() {
        throw new NullPointerException();
    }
}
