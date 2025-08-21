/**
 * 
 */
package me.ehp246.test.embedded.returns;

import java.util.UUID;
import java.util.random.RandomGenerator;

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
class XmlTest {
    @Autowired
    private XmlTestCase case01;

    @Test
    void test_01() {
        final var count = Math.abs(RandomGenerator.getDefault().nextInt()) % 10 + 1;
        final var xml = case01.get01(count);

        Assertions.assertEquals(true, xml.length() > 10);
    }

    @Test
    void test_02() {
        final var text = UUID.randomUUID().toString();

        Assertions.assertEquals(text, case01.getText(text));
    }

    @Test
    void test_03() {
        final var text = UUID.randomUUID().toString();
        Assertions.assertEquals(true, case01.getPerson(text).contains(text));
    }
}
