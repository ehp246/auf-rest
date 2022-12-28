/**
 * 
 */
package me.ehp246.test.local.returntype;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class XmlTest {
    @Autowired
    private XmlTestCase001 case001;

    @Disabled
    @Test
    void test_001() {
        final var count = (int) (Math.random() * 10);
        final var instants = case001.get001(count);

        Assertions.assertEquals(count, instants.size());

        instants.stream().forEach(instant -> Assertions.assertEquals(true, instant instanceof Instant));
    }

    @Test
    void test_002() {
        final var count = (int) (Math.random() * 10) + 10;
        final var xml = case001.get006(count);

        Assertions.assertEquals(true, xml.length() > 10);
    }

    @Test
    void test_003() {
        final var text = UUID.randomUUID().toString();

        Assertions.assertEquals(text, case001.getText(text));
    }

    @Test
    void test_004() {
        final var text = UUID.randomUUID().toString();
        Assertions.assertEquals(true, case001.getPerson(text).contains(text));
    }
}
