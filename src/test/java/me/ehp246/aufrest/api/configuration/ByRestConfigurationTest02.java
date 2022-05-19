package me.ehp246.aufrest.api.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestClientConfig;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AufRestConfiguration.class, Jackson.class })
@TestPropertySource(properties = { "me.ehp246.aufrest.connectTimeout=PT0.01S",
        "me.ehp246.aufrest.responseTimeout=PT0.1S" })
class ByRestConfigurationTest02 {
    @Autowired
    private ListableBeanFactory beanFactory;

    @Test
    void timeout_001() {
        Assertions.assertEquals("PT0.01S", beanFactory.getBean(RestClientConfig.class).connectTimeout().toString());
    }

    @Test
    void timeout_002() {
        Assertions.assertEquals("PT0.1S", beanFactory.getBean(RequestBuilder.class).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost";
            }
        }).timeout().get().toString());
    }
}
