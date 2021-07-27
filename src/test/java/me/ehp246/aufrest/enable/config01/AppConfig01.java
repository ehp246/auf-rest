package me.ehp246.aufrest.enable.config01;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@EnableByRest
@Import(Jackson.class)
public class AppConfig01 {

}
