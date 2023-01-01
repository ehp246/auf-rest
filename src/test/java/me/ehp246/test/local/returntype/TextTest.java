package me.ehp246.test.local.returntype;

import java.time.Instant;
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
class TextTest {
    @Autowired
    private TextTestCase textCase001;

    @Test
    void text_test_001() {
        final var instant = textCase001.get();

        Assertions.assertDoesNotThrow(() -> Instant.parse(instant));
    }

    @Test
    void text_test_002() {
        final var now = Instant.now();
        final var returned = textCase001.post(now);

        Assertions.assertEquals(now.toString(), returned);
    }

    @Test
    void text_test_003() {
        Assertions.assertDoesNotThrow(() -> Instant.parse(textCase001.getJson().replace("\"", "")));
    }

    @Test
    void text_004() {
        final var name = UUID.randomUUID().toString();
        
        Assertions.assertEquals(true, textCase001.getPerson(name).startsWith("{\"name\":\"" + name));
    }
}
