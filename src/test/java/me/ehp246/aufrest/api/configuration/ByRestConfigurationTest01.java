package me.ehp246.aufrest.api.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.rest.HttpClientConfig;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestHttpRequestBuilder;
import me.ehp246.aufrest.core.byrest.AufRestConfiguration;
import me.ehp246.aufrest.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AufRestConfiguration.class)
@Import(Jackson.class)
class ByRestConfigurationTest01 {
    @Autowired
    private ListableBeanFactory beanFactory;

    @Test
    void test_001() {
        final var clientConfig = new AufRestConfiguration().restClientConfig("");

        Assertions.assertEquals(null, clientConfig.connectTimeout());
    }

    @Test
    void test_002() {
        final var clientConfig = new AufRestConfiguration().restClientConfig(null);

        Assertions.assertEquals(null, clientConfig.connectTimeout());
    }

    @Test
    void test_004() {
        Assertions.assertDoesNotThrow(() -> new AufRestConfiguration().restClientConfig(null));
    }

    @Test
    void test_005() {
        final var clientConfig = new AufRestConfiguration().restClientConfig("PT1S");

        Assertions.assertEquals(1000, clientConfig.connectTimeout().toMillis());
    }

    @Test
    void timeout_001() {
        Assertions.assertEquals(null, beanFactory.getBean(HttpClientConfig.class).connectTimeout());
    }

    @Test
    void timeout_002() {
        Assertions.assertEquals(true, beanFactory.getBean(RestHttpRequestBuilder.class).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost";
            }
        }).timeout().isEmpty());
    }
}
