package me.ehp246.test.storage;

import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import me.ehp246.test.TestConfig;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("local")
@EnabledIfEnvironmentVariable(named = "me.ehp246.test", matches = "true")
class BlobTest {
    private static final Logger LOGGER = LogManager.getLogger();
    @Autowired
    private BlobContainer container;

    @Test
    void upload_01() throws IOException {
        container.put(UUID.randomUUID().toString() + ".txt",
                new ClassPathResource(TestConfig.SAMPLE_TEXT).getFile().toPath());
    }

    @Test
    void get_01() throws IOException {
        final var text = container.get("Perf.txt");

        LOGGER.info(text);
    }
}
