package me.ehp246.aufrest.enable;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.enable.config02.Case02;
import me.ehp246.aufrest.enable.config03.Case03;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
class AppConfig {
    @EnableByRest
    @Import(Jackson.class)
    static class Config01 {

    }

    @EnableByRest(scan = { Case02.class, Case03.class })
    @Import(Jackson.class)
    static class Config02 {

    }
}
