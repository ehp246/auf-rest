package me.ehp246.aufrest.integration.local.filter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.RequestFilter;
import me.ehp246.aufrest.api.rest.ResponseFilter;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import(Jackson.class)
class AppConfig {
	@Bean
	public RequestFilter requestFilter(final ReqFilter reqFilter) {
		return reqFilter::apply;
	}
	
	@Bean
	public ResponseFilter responseFilter(final RespFilter respFilter) {
		return respFilter::apply;
	}
}
