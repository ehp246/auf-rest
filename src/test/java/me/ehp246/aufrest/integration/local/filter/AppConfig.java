package me.ehp246.aufrest.integration.local.filter;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.RestLogger;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import({ Jackson.class, RestLogger.class })
class AppConfig {
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
