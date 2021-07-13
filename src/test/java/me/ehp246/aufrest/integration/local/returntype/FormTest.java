package me.ehp246.aufrest.integration.local.returntype;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    private FormTestCase001 case001;

    @Test
    void post_001() {
        case001.post("name=" + URLEncoder.encode("???", StandardCharsets.UTF_8));
    }
}
