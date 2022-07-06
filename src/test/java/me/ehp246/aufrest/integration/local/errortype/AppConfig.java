package me.ehp246.aufrest.integration.local.errortype;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@Import(Jackson.class)
@EnableByRest()
class AppConfig {
}
