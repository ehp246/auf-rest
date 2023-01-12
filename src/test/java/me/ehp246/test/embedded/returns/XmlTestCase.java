package me.ehp246.test.embedded.returns;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;

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
    @OfBody(Person.class)
    List<Person> get008();

    // Response types

    @OfRequest("person")
    @OfBody(Person.class)
    HttpResponse<Person> get011();

    @OfRequest("instants")
    @OfBody({ List.class, Instant.class })
    HttpResponse<List<Instant>> get002(@OfQuery("count") int count);

    @OfRequest("instants")
    @OfBody({ List.class, Instant.class })
    HttpResponse get004(@OfQuery("count") int count);

    @OfRequest("instants")
    @AsIs
    @OfBody(String.class)
    HttpResponse<String> get007(@OfQuery("count") int count);

    // Future types

    @OfRequest("person")
    @OfBody(Person.class)
    CompletableFuture<Person> get010();

    @OfRequest("persons")
    @OfBody({ List.class, Person.class })
    CompletableFuture<List<Person>> get009();

    @OfRequest("instants")
    @OfBody({ HttpResponse.class, List.class, Instant.class })
    CompletableFuture<HttpResponse<List<Instant>>> get003(@OfQuery("count") int count);

    @OfRequest("instants")
    @AsIs
    @OfBody({ HttpResponse.class, String.class })
    CompletableFuture<HttpResponse<String>> get008(@OfQuery("count") int count);
}