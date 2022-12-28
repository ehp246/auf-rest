package me.ehp246.test.local.listener;

import java.net.http.HttpRequest;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.RestListener;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import({ Jackson.class })
class AppConfig {
    @Bean
    @Order(2)
    Listener reqConsumer01() {
        return new Listener(2);
    }

    @Bean
    @Order(1)
    Listener reqConsumer02() {
        return new Listener(1);
    }

    @Bean
    @Profile("listenerEx")
    RestListener listenerEx() {
        return new RestListener() {

            @Override
            public void onRequest(HttpRequest httpRequest, RestRequest req) {
                throw new NullPointerException("onRequest");
            }

        };
    }
}
