package me.ehp246.test.app.bean;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.PlaceholderResolutionException;

import me.ehp246.aufrest.api.spi.ExpressionResolver;

@SpringBootTest(classes = { AppConfig.class }, properties = {
        "rest.server.api=http://api.rest.server" }, webEnvironment = WebEnvironment.NONE)
@DirtiesContext
class BeanTest {
    @Autowired
    private ExpressionResolver resolver;
    @Autowired
    private AppConfig.RestServerConfig config;

    @Test
    void propertyResolver_01() {
        Assertions.assertEquals(config.api(), resolver.resolve("${rest.server.api}"));
    }

    @Test
    void propertyResolver_02() {
        Assertions.assertEquals("prefix-" + config.api() + "/v1", resolver.resolve("prefix-${rest.server.api}/v1"));
    }

    @Test
    void propertyResolver_03() {
        Assertions.assertEquals(config.api(),
                resolver.resolve("#{@'rest.server-me.ehp246.test.app.bean.AppConfig$RestServerConfig'.api}"));
    }

    @Test
    void propertyResolver_04() {
        Assertions.assertThrows(PlaceholderResolutionException.class, () -> resolver.resolve("${not.there}"));
    }

    @Test
    void propertyResolver_05() {
        Assertions.assertEquals("prefix-" + config.api() + "/v1", resolver.resolve(
                "#{'prefix-' + @'rest.server-me.ehp246.test.app.bean.AppConfig$RestServerConfig'.api + '/v1'}"));
    }
}
