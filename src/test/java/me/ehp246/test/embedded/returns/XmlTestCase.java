package me.ehp246.test.embedded.returns;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;
import me.ehp246.aufrest.api.annotation.OfResponse.BodyOf;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest(value = "http://localhost:${local.server.port}/xml/")
interface XmlTestCase {
    @OfRequest(value = "text/{text}", accept = "application/xml")
    String getText(@OfPath("text") String text);

    // Accept XML, return as String
    @OfRequest(value = "instants", accept = "application/xml")
    String get01(@OfQuery("count") int count);

    @OfRequest(value = "person", accept = "application/xml")
    String getPerson(@OfQuery("name") String name);

    @OfRequest(value = "persons", contentType = "application/xml", accept = "application/xml")
    @OfResponse(bodyOf = @BodyOf(Person.class))
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