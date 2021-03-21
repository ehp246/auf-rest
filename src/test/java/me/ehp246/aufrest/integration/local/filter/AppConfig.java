package me.ehp246.aufrest.integration.local.filter;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.RequestByRest;
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
	@Autowired
	private TestCase001 case001;

	@Bean
	public RequestFilter requestFilter() {
		return new RequestFilter() {
			
			@Override
			public HttpRequest apply(HttpRequest httpRequest, RequestByRest req) {
				if (req.invokedOn().target() != case001)
					return httpRequest;
				return httpRequest;
			}
		};
	}
	
	@Bean
	public ResponseFilter responseFilter() {
		return new ResponseFilter() {
			
			@Override
			public HttpResponse<?> apply(HttpResponse<?> response, RequestByRest req) {
				return response;
			}
		};
	}
}
