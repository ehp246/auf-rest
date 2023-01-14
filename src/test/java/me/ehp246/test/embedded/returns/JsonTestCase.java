package me.ehp246.test.embedded.returns;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;
import me.ehp246.aufrest.api.annotation.OfResponse.BodyOf;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("http://localhost:${local.server.port}/json/")
interface JsonTestCase {
    @OfRequest("instants")
    @OfResponse(bodyOf = @BodyOf({ ArrayList.class, Instant.class }))
    List<Instant> get001(@OfQuery("count") int count);

    @OfRequest("instants")
    String get006(@OfQuery("count") int count);

    @OfRequest("person")
    Person get007();

    @OfRequest("person")
    Person getZip(@OfHeader("accept-encoding") String acceptEncoding);

    @OfRequest("null")
    Person getNull();

    @OfRequest("double")
    double getDouble001();

    @OfRequest("double")
    Double getDouble002();

    @OfRequest("204")
    Person getStatus204();

    @OfRequest("persons")
    @OfResponse(bodyOf = @BodyOf({ ArrayList.class, Person.class }))
    List<Person> get008();

    // Response types

    @OfRequest("person")
    @OfResponse(bodyOf = @BodyOf(Person.class))
    HttpResponse<Person> get011();

    @OfRequest("instants")
    @OfResponse(bodyOf = @BodyOf({ List.class, Instant.class }))
    HttpResponse<List<Instant>> get002(@OfQuery("count") int count);

    @OfRequest("instants")
    @OfResponse(bodyOf = @BodyOf({ List.class, Instant.class }))
    HttpResponse get004(@OfQuery("count") int count);

    @OfRequest("instants")
    @OfResponse(bodyOf = @BodyOf(String.class))
    HttpResponse<String> get007(@OfQuery("count") int count);
}