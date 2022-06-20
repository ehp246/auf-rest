package me.ehp246.aufrest.it.config02;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.configuration.EnableByRest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@EnableByRest
@Import(Jackson.class)
class AppConfig02 {
    static final String BEAN_NAME = "30f0b393-0a64-4b75-a5d8-2737cba10508";
}
