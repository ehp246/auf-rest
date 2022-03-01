package me.ehp246.aufrest.integration.local.mime.form;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class FormTest {
    @Autowired
    private FormCase formCase;

    @Test
    void post_01() {
        final var name = UUID.randomUUID().toString();

        Assertions.assertEquals(name, formCase.post(name));
    }
}
