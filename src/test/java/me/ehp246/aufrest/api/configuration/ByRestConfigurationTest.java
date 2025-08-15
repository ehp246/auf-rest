package me.ehp246.aufrest.api.configuration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.AufRestConfiguration;
import me.ehp246.aufrest.core.rest.HttpRequestBuilder;
import me.ehp246.test.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AufRestConfiguration.class)
@Import(Jackson.class)
class ByRestConfigurationTest {
    @Autowired
    private ListableBeanFactory beanFactory;

    @Test
    void test_001() {
        Assertions.assertEquals(true,
                new AufRestConfiguration().httpClientBuilderSupplier("").get().build().connectTimeout().isEmpty());
    }

    @Test
    void test_002() {
        Assertions.assertEquals(true,
                new AufRestConfiguration().httpClientBuilderSupplier(null).get().build().connectTimeout().isEmpty());
    }

    @Test
    void test_004() {
        Assertions.assertDoesNotThrow(() -> new AufRestConfiguration().httpClientBuilderSupplier(null));
    }

    @Test
    void test_005() {
        Assertions.assertEquals(1000, new AufRestConfiguration().httpClientBuilderSupplier("PT1S").get().build()
                .connectTimeout().get().toMillis());
    }

    @Test
    void timeout_002() {
        Assertions.assertEquals(true, beanFactory.getBean(HttpRequestBuilder.class).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost";
            }
        }).timeout().isEmpty());
    }
}
