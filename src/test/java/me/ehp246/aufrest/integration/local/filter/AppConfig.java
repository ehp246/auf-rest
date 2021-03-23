package me.ehp246.aufrest.integration.local.filter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.RequestFilter;
import me.ehp246.aufrest.api.rest.RequestLogger;
import me.ehp246.aufrest.api.rest.ResponseFilter;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import({ Jackson.class, RequestLogger.class })
class AppConfig {
	@Bean
	RequestFilter requestFilter(final ReqFilter reqFilter) {
		return reqFilter::apply;
	}
	
	@Bean
	ResponseFilter responseFilter(final RespFilter respFilter) {
		return respFilter::apply;
	}

	@Bean
	@Order(2)
	ReqConsumer reqConsumer01() {
		return new ReqConsumer(2);
	}

	@Bean
	@Order(1)
	ReqConsumer reqConsumer02() {
		return new ReqConsumer(1);
	}
}
