package me.ehp246.test.embedded.query;

import java.util.List;
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
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=true" })
class QueryTest {
    @Autowired
    private QueryCase testCase;

    @Test
    void test_01() {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();

        final var got = testCase.getSingle(firstName, lastName);

        Assertions.assertEquals(2, got.entrySet().size());
        Assertions.assertEquals(firstName, got.get("firstName"));
        Assertions.assertEquals(lastName, got.get("lastName"));
    }


    @Test
    void test_02() {
        final var firstName = UUID.randomUUID().toString();
        final var lastName = UUID.randomUUID().toString();

        final var got = testCase.getList(List.of(firstName, lastName), List.of(lastName, firstName));

        Assertions.assertEquals(2, got.entrySet().size());
        Assertions.assertEquals(2, got.get("firstName").size());
        Assertions.assertEquals(2, got.get("lastName").size());

        Assertions.assertEquals(firstName, got.get("firstName").get(0));
        Assertions.assertEquals(lastName, got.get("firstName").get(1));
        Assertions.assertEquals(lastName, got.get("lastName").get(0));
        Assertions.assertEquals(firstName, got.get("lastName").get(1));
    }

    @Test
    void test_03() {
        final var firstName = "First %# name >& шеллы ";
        final var lastName = "last/name <=:? 于蕾任 2023 春晚总导演";

        final var got = testCase.getSingle(firstName, lastName);

        Assertions.assertEquals(2, got.entrySet().size());
        Assertions.assertEquals(firstName, got.get("firstName"));
        Assertions.assertEquals(lastName, got.get("lastName"));
    }
}
