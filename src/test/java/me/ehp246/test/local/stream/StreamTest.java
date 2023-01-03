package me.ehp246.test.local.stream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restLogger.enabled:true" })
class StreamTest {
    @Autowired
    private StreamCase.Case001 case001;

    @Autowired
    private StreamCase.Case002 case002;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void stream_001() throws JsonParseException, JsonMappingException, IOException {
        final var in = case001.get("Jon Snow");

        Assertions.assertTrue(in instanceof GZIPInputStream);

        final var person = objectMapper.readValue(in, Person.class);

        Assertions.assertTrue(person.getName().equals("Jon Snow"));
        Assertions.assertTrue(person.getDob() instanceof Instant);
    }

    @Test
    void stream_002() throws JsonParseException, JsonMappingException, IOException {
        final var in = case001.get002("Jon Snow").body();

        Assertions.assertTrue(in instanceof GZIPInputStream);

        final var person = objectMapper.readValue(in, Person.class);

        Assertions.assertTrue(person.getName().equals("Jon Snow"));
        Assertions.assertTrue(person.getDob() instanceof Instant);
    }

    @Test
    void json_002() throws JsonParseException, JsonMappingException, IOException {
        final var in = case002.get("Jon Snow");

        Assertions.assertFalse(in instanceof GZIPInputStream);

        final var person = objectMapper.readValue(in, Person.class);

        Assertions.assertTrue(person.getName().equals("Jon Snow"));
        Assertions.assertTrue(person.getDob() instanceof Instant);
    }

    @Test
    void json_003() {
        final var count = (int) (Math.random() * 100);

        Assertions.assertEquals(count, case001.post(new ByteArrayInputStream(new byte[count])).intValue());
    }
}
