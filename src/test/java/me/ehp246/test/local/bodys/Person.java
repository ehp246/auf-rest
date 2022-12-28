package me.ehp246.test.local.bodys;

import java.time.Instant;

/**
 * @author Lei Yang
 *
 */
record Person(String firstName, String lastName, Instant dob) implements PersonName, PersonDob {
}
