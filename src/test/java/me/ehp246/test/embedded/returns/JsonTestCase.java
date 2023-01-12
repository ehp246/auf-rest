package me.ehp246.test.embedded.returns;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;
import me.ehp246.aufrest.api.annotation.OfResponse.Body;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("http://localhost:${local.server.port}/json/")
interface JsonTestCase {
    @OfRequest("instants")
    @OfResponse(body = @Body({ ArrayList.class, Instant.class }))
    List<Instant> get001(@OfQuery("count") int count);

    @OfRequest("instants")
    @AsIs
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
    @OfResponse(body = @Body({ ArrayList.class, Person.class }))
    List<Person> get008();

    // Response types

    @OfRequest("person")
    @OfResponse(body = @Body(Person.class))
    HttpResponse<Person> get011();

    @OfRequest("instants")
    @OfResponse(body = @Body({ List.class, Instant.class }))
    HttpResponse<List<Instant>> get002(@OfQuery("count") int count);

    @OfRequest("instants")
    @OfResponse(body = @Body({ List.class, Instant.class }))
    HttpResponse get004(@OfQuery("count") int count);

    @OfRequest("instants")
    @AsIs
    @OfResponse(body = @Body(String.class))
    HttpResponse<String> get007(@OfQuery("count") int count);
}