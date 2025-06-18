package me.ehp246.test.app.objectmapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.provider.jackson.JsonByJackson;
import me.ehp246.test.mock.Jackson;

/**
 * @author Lei Yang
 *
 */
class ObjectMapperTest {
    private AnnotationConfigApplicationContext appCtx;

    @AfterEach
    void close() {
        if (appCtx != null) {
            appCtx.close();
            appCtx = null;
        }
    }

    @Test
    void provided_01() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config01.class);

        Assertions.assertEquals(true, appCtx.getBean(JsonByJackson.class) != null);
    }

    @Test
    void provided_02() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config04.class);

        Assertions.assertEquals(true, appCtx.getBean(JsonByJackson.class) != null);
    }

    @Test
    void provided_03() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config06.class);

        Assertions.assertEquals(true, appCtx.getBean(JsonByJackson.class) != null);
    }

    @Test
    void provided_04() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config07.class);

        Assertions.assertEquals(true, appCtx.getBean(JsonByJackson.class) != null);
    }

    @Test
    void primary_01() {
        appCtx = new AnnotationConfigApplicationContext();
        appCtx.register(AppConfig.Config02.class);
        appCtx.refresh();

        Assertions.assertEquals(Jackson.OBJECT_MAPPER, appCtx.getBean(ObjectMapper.class));
    }

    @Test
    void private_01() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config03.class);

        Assertions.assertEquals(true, appCtx.getBean(JsonByJackson.class) != null);
    }

    @Test
    void private_02() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config05.class);

        Assertions.assertEquals(true, appCtx.getBean(JsonByJackson.class) != null);
    }

    @Test
    void private_03() {
        appCtx = new AnnotationConfigApplicationContext(AppConfig.Config08.class);

        Assertions.assertEquals(true, appCtx.getBean(JsonByJackson.class) != null);
    }
}
