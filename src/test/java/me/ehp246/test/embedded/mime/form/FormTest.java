package me.ehp246.test.embedded.mime.form;

import java.time.Instant;
import java.util.List;
import java.util.Map;
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
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=true" })
class FormTest {
    @Autowired
    private FormCase formCase;

    @Test
    void post_01() {
        final var name = UUID.randomUUID().toString();

        Assertions.assertEquals(name, formCase.post(name));
    }

    @Test
    void post_02() {
        final var now = Instant.now();
        final var person = formCase.postQueryInBody(null, "", now);

        Assertions.assertEquals(true, now.equals(person.dob()));
        Assertions.assertEquals(null, person.firstName());
        Assertions.assertEquals(true, person.lastName() == "");
    }

    @Test
    void post_03() {
        final var now = Instant.now();
        final var person = formCase.postQueryOnPath("", "", now);

        Assertions.assertEquals(true, now.equals(person.dob()));
        Assertions.assertEquals(true, person.firstName() == "");
        Assertions.assertEquals(true, person.lastName() == "");
    }

    @Test
    void post_04() {
        final var now = Instant.now();
        final var person = formCase.postQueryOnPath(null, now.toString(), now);

        Assertions.assertEquals(true, now.equals(person.dob()));
        Assertions.assertEquals(null, person.firstName());
        Assertions.assertEquals(true, person.lastName().equals(now.toString()));
    }

    @Test
    void queryMap_01() {
        Assertions.assertEquals(4,
                formCase.postQueryMap(Map.of("qList", "p1"), List.of("p2", "p3"), "p4").size());
    }
}
