package me.ehp246.test.embedded.resttemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfQuery;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/resttemplate/")
interface PersonProxy {
    @OfRequest("person")
    Person get(@OfQuery String firstName, @OfQuery String lastName, @OfQuery Instant dob);

    @OfRequest("person")
    Person get(@OfQuery Map<String, List<String>> queries);

    @OfRequest("person/{firstName}/{lastName}")
    Person post(@OfPath String firstName, @OfPath String lastName, Person person);
}
