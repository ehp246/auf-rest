package me.ehp246.test.embedded.body;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface BodyAsCase {
    @OfRequest("/person")
    Person post(Person person);

    @OfRequest("/personName")
    Person post(PersonName name);

    @OfRequest("/personDob")
    Person post(PersonDob dob);
}
