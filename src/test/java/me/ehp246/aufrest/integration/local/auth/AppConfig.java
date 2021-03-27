package me.ehp246.aufrest.integration.local.auth;

import java.util.concurrent.atomic.AtomicReference;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import me.ehp246.aufrest.api.annotation.EnableByRest;
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
	public static void main(final String[] args) {
		SpringApplication.run(AppConfig.class, args);
	}

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
}
