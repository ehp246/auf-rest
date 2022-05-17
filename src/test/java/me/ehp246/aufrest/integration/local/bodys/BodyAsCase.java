package me.ehp246.aufrest.integration.local.bodys;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface BodyAsCase {
    @OfMapping("/person")
    Person post(Person person);

    @OfMapping("/personName")
    Person post(PersonName name);

    @OfMapping("/personDob")
    Person post(PersonDob dob);
}
