package me.ehp246.test.embedded.resttemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.provider.TimingExtension;

/**
 *
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = AppConfig.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(TimingExtension.class)
class RestTemplateTest {
    private static final int COUNT = 10_000;
    @Autowired
    private PersonProxy proxy;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestFn restFn;

    @Value("${local.server.port}")
    private int port;

    /**
     * 15,419 ms
     */
    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.aufrest", matches = "true")
    void proxy_01() {
        for (int i = 0; i <= COUNT; i++) {
            final var person = proxy.get(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Instant.now());
            Assertions.assertEquals(true, person != null);
        }
    }

    /**
     * 16,563 ms
     */
    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.aufrest", matches = "true")
    void proxy_02() {
        final List<String> firstName = new ArrayList<>(1);
        firstName.add(null);

        final List<String> lastName = new ArrayList<>(1);
        lastName.add(null);

        final List<String> dob = new ArrayList<>(1);
        dob.add(null);

        final var queries = Map.of("firstName", firstName, "lastName", lastName, "dob", dob);

        for (int i = 0; i <= COUNT; i++) {
            firstName.set(0, UUID.randomUUID().toString());
            lastName.set(0, UUID.randomUUID().toString());
            dob.set(0, Instant.now().toString());

            final var person = proxy.get(queries);
            Assertions.assertEquals(true, person != null);
        }
    }

    /**
     * 16,758 ms
     */
    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.aufrest", matches = "true")
    void proxy_03() {
        for (int i = 0; i <= COUNT; i++) {
            final var person = proxy.post(UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                    new Person(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Instant.now()));
            Assertions.assertEquals(true, person != null);
        }
    }

    /**
     * 13,747 ms
     */
    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.aufrest", matches = "true")
    void resttemplate_01() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        final var httpEntity = new HttpEntity<>(headers);

        final var url = "http://localhost:" + port
                + "/resttemplate/person?firstName={firstName}&lastName={lastName}&dob={dob}";

        for (int i = 0; i <= COUNT; i++) {
            final var person = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Person.class,
                    UUID.randomUUID().toString(), UUID.randomUUID().toString(), Instant.now().toString());
            Assertions.assertEquals(true, person != null);
        }
    }

    /**
     * 26,392 ms
     */
    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.aufrest", matches = "true")
    void resttemplate_02() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        final var url = "http://localhost:" + port + "/resttemplate/person/{firstName}/{lastName}";

        for (int i = 0; i <= COUNT; i++) {
            final var httpEntity = new HttpEntity<>(
                    new Person(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Instant.now()), headers);
            final var person = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Person.class,
                    UUID.randomUUID().toString(), UUID.randomUUID().toString());
            Assertions.assertEquals(true, person != null);
        }
    }

    /**
     * 13,145 ms
     */
    @Test
    @EnabledIfSystemProperty(named = "me.ehp246.aufrest", matches = "true")
    void restFn_01() {
        final var uri = "http://localhost:" + port + "/resttemplate/person";

        final List<String> firstName = new ArrayList<>(1);
        firstName.add(null);

        final List<String> lastName = new ArrayList<>(1);
        lastName.add(null);

        final List<String> dob = new ArrayList<>(1);
        dob.add(null);

        final var queries = Map.of("firstName", firstName, "lastName", lastName, "dob", dob);

        final var mockRequest = new MockQuery();
        mockRequest.uri = uri;
        mockRequest.queries = queries;

        for (int i = 0; i <= COUNT; i++) {
            firstName.set(0, UUID.randomUUID().toString());
            lastName.set(0, UUID.randomUUID().toString());
            dob.set(0, Instant.now().toString());

            final var person = restFn.apply(mockRequest, Person.class);

            Assertions.assertEquals(true, person != null);
        }
    }
}
