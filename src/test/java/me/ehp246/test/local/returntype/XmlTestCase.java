package me.ehp246.test.local.returntype;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.ReifyingBody;
import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest(value = "http://localhost:${local.server.port}/xml/")
interface XmlTestCase {
    @OfMapping(value = "text/{text}", accept = "application/xml")
    String getText(@PathVariable("text") String text);

    // Accept XML, return as String
    @OfMapping(value = "instants", accept = "application/xml")
    String get01(@RequestParam("count") int count);

    @OfMapping(value = "person", accept = "application/xml")
    String getPerson(@RequestParam("name") String name);

    @OfMapping(value = "persons", contentType = "application/xml", accept = "application/xml")
    @ReifyingBody(Person.class)
    List<Person> get008();

    // Response types

    @OfMapping("person")
    @ReifyingBody(Person.class)
    HttpResponse<Person> get011();

    @OfMapping("instants")
    @ReifyingBody({ List.class, Instant.class })
    HttpResponse<List<Instant>> get002(@RequestParam("count") int count);

    @OfMapping("instants")
    @ReifyingBody({ List.class, Instant.class })
    HttpResponse get004(@RequestParam("count") int count);

    @OfMapping("instants")
    @AsIs
    @ReifyingBody(String.class)
    HttpResponse<String> get007(@RequestParam("count") int count);

    // Future types

    @OfMapping("person")
    @ReifyingBody(Person.class)
    CompletableFuture<Person> get010();

    @OfMapping("persons")
    @ReifyingBody({ List.class, Person.class })
    CompletableFuture<List<Person>> get009();

    @OfMapping("instants")
    @ReifyingBody({ HttpResponse.class, List.class, Instant.class })
    CompletableFuture<HttpResponse<List<Instant>>> get003(@RequestParam("count") int count);

    @OfMapping("instants")
    @AsIs
    @ReifyingBody({ HttpResponse.class, String.class })
    CompletableFuture<HttpResponse<String>> get008(@RequestParam("count") int count);
}