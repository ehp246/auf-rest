package me.ehp246.test.embedded.body;

import java.net.http.HttpResponse.BodyHandler;
import java.util.UUID;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.test.mock.Jackson;
import me.ehp246.test.mock.MockBodyHandler;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import(Jackson.class)
class AppConfig {
    static final String METHOD_HANDLER = UUID.randomUUID().toString();

    @Bean("onInterface")
    BodyHandler<String> interfaceHander() {
        return new MockBodyHandler<String>("interface");
    }

    @Bean("onMethod")
    BodyHandler<String> methodHander() {
        return new MockBodyHandler<String>(METHOD_HANDLER);
    }
}
