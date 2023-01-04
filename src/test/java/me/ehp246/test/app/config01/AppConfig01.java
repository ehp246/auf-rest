package me.ehp246.test.app.config01;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.test.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@EnableByRest
@Import(Jackson.class)
class AppConfig01 {

}
