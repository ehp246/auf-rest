package me.ehp246.test.embedded.returns;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class TextTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TextCase testCase;

    @Test
    void text_test_001() {
        final var instant = testCase.get();

        Assertions.assertDoesNotThrow(() -> Instant.parse(instant));
    }

    @Test
    void text_test_002() {
        final var now = Instant.now();
        final var returned = testCase.post(now);

        Assertions.assertEquals(now.toString(), returned);
    }

    @Test
    void text_test_003() {
        Assertions.assertDoesNotThrow(() -> Instant.parse(testCase.getJson().replace("\"", "")));
    }

    @Test
    void text_04() throws JsonMappingException, JsonProcessingException {
        final var expected = UUID.randomUUID().toString();

        final var result = this.objectMapper.readValue(testCase.getPerson(expected), Map.class);

        Assertions.assertEquals(expected, result.get("name"));
    }
}
