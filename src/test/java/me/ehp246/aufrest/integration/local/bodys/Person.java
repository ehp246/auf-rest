package me.ehp246.aufrest.integration.local.bodys;

import java.time.Instant;

/**
 * @author Lei Yang
 *
 */
record Person(String firstName, String lastName, Instant dob) implements PersonName, PersonDob {
}
