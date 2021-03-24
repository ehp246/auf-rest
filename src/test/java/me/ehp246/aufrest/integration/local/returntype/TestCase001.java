package me.ehp246.aufrest.integration.local.returntype;

import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.AsIs;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.annotation.Reifying;
import me.ehp246.aufrest.integration.model.Person;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("http://localhost:${local.server.port}/returntype/")
interface TestCase001 {
	// Body types

	@OfMapping("instants")
	@Reifying(Instant.class)
	List<Instant> get001(@RequestParam("count") int count);

	@OfMapping("instants")
	@AsIs
	String get006(@RequestParam("count") int count);

	@OfMapping("person")
	Person get007();

	@OfMapping("persons")
	@Reifying(Person.class)
	List<Person> get008();

	// Response types

	@OfMapping("person")
	@Reifying(Person.class)
	HttpResponse<Person> get011();

	@OfMapping("instants")
	@Reifying({ List.class, Instant.class })
	HttpResponse<List<Instant>> get002(@RequestParam("count") int count);

	@OfMapping("instants")
	@Reifying({ List.class, Instant.class })
	HttpResponse get004(@RequestParam("count") int count);

	@OfMapping("instants")
	@AsIs
	@Reifying(String.class)
	HttpResponse<String> get007(@RequestParam("count") int count);

	// Future types

	@OfMapping("person")
	@Reifying(Person.class)
	CompletableFuture<Person> get010();

	@OfMapping("persons")
	@Reifying({ List.class, Person.class })
	CompletableFuture<List<Person>> get009();

	@OfMapping("instants")
	@Reifying({ HttpResponse.class, List.class, Instant.class })
	CompletableFuture<HttpResponse<List<Instant>>> get003(@RequestParam("count") int count);

	@OfMapping("instants")
	@AsIs
	@Reifying({ HttpResponse.class, String.class })
	CompletableFuture<HttpResponse<String>> get008(@RequestParam("count") int count);
}