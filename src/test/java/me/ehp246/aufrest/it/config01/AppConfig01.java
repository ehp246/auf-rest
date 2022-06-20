package me.ehp246.aufrest.it.config01;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.configuration.EnableByRest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@EnableByRest
@Import(Jackson.class)
class AppConfig01 {

}
