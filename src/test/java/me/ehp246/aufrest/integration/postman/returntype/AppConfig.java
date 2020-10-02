package me.ehp246.aufrest.integration.postman.returntype;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.provider.jackson.JsonByJacksonTest;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication
@EnableByRest
class AppConfig {
	@Bean
	public ObjectMapper objectMapper() {
		return JsonByJacksonTest.OBJECT_MAPPER;
	}
}
