package me.ehp246.test.local.returns;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("http://localhost:${local.server.port}/json/")
interface JsonTestCase {
    @OfMapping("instants")
    @OfBody({ ArrayList.class, Instant.class })
    List<Instant> get001(@RequestParam("count") int count);

    @OfMapping("instants")
    @AsIs
    String get006(@RequestParam("count") int count);

    @OfMapping("person")
    Person get007();

    @OfMapping("person")
    Person getZip(@OfHeader("accept-encoding") String acceptEncoding);

    @OfMapping("null")
    Person getNull();

    @OfMapping("double")
    double getDouble001();

    @OfMapping("double")
    Double getDouble002();

    @OfMapping("204")
    Person getStatus204();

    @OfMapping("persons")
    @OfBody({ ArrayList.class, Person.class })
    List<Person> get008();

    // Response types

    @OfMapping("person")
    @OfBody(Person.class)
    HttpResponse<Person> get011();

    @OfMapping("instants")
    @OfBody({ List.class, Instant.class })
    HttpResponse<List<Instant>> get002(@RequestParam("count") int count);

    @OfMapping("instants")
    @OfBody({ List.class, Instant.class })
    HttpResponse get004(@RequestParam("count") int count);

    @OfMapping("instants")
    @AsIs
    @OfBody(String.class)
    HttpResponse<String> get007(@RequestParam("count") int count);
}