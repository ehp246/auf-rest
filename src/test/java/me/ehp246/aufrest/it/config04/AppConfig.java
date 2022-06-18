package me.ehp246.aufrest.it.config04;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.configuration.EnableByRest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableByRest
    @Import(Jackson.class)
    static class Config01 {
        @ByRest("${url}")
        interface Case01 {
        }
    }

    @EnableByRest
    @Import(Jackson.class)
    static class Config02 {

    }
}
