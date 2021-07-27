package me.ehp246.aufrest.enable.config02;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@EnableByRest
@Import(Jackson.class)
public class AppConfig02 {
    public static final String BEAN_NAME = "30f0b393-0a64-4b75-a5d8-2737cba10508";
}
