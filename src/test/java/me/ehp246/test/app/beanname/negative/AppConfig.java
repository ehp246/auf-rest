package me.ehp246.test.app.beanname.negative;

import java.util.UUID;

import org.springframework.context.annotation.Bean;

import me.ehp246.aufrest.api.annotation.EnableByRest;

/**
 * @author Lei Yang
 *
 */
@EnableByRest
class AppConfig {
    @Bean
    String case01() {
        return UUID.randomUUID().toString();
    }
}
