package me.ehp246.aufrest.integration.local.header;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByRest
@Import(Jackson.class)
class AppConfig {
	public static final List<String> NAMES = List.of("provider-header-0", "provider-header-1");
	public static final List<String> VALUES = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
			UUID.randomUUID().toString());

	@Bean
	public HeaderProvider headerProvider() {
		return req -> Map.of(NAMES.get(0), List.of(VALUES.get(0)), NAMES.get(1), List.of(VALUES.get(1), VALUES.get(2)));
	}
}
