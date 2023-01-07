package me.ehp246.test.embedded.restfn;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.test.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByRest
@Import(Jackson.View.class)
class AppConfig {
    @Bean
    AuthProvider authProvider() {
        return req -> "Basic YmFzaWN1c2VyOnBhc3N3b3Jk";
    }
}
