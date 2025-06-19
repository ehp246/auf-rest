package me.ehp246.test.embedded.returns;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("http://localhost:${local.server.port}/json/")
interface JsonTestCase {
    @OfRequest("instants")
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
    List<Person> get008();

    // Response types

    @OfRequest("person")
    HttpResponse<Person> get011();

    @OfRequest("instants")
    HttpResponse<List<Instant>> get002(@OfQuery("count") int count);

    @OfRequest("instants")
    HttpResponse<List<Instant>> get004(@OfQuery("count") int count);

    @OfRequest("instants")
    HttpResponse<String> get007(@OfQuery("count") int count);
}