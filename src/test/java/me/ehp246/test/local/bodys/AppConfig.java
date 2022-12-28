package me.ehp246.test.local.bodys;

import java.net.http.HttpResponse.BodyHandler;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.mock.Jackson;
import me.ehp246.aufrest.mock.MockResponseBodyHandler;

/**
 * @author Lei Yang
 *
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableByRest
@Import(Jackson.class)
class AppConfig {
    @Bean("onInterface")
    BodyHandler<String> interfaceHander() {
        return new MockResponseBodyHandler<String>("interface");
    }

    @Bean("onMethod")
    BodyHandler<String> methodHander() {
        return new MockResponseBodyHandler<String>("method");
    }
}
