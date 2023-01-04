package me.ehp246.test.embedded.body;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lei Yang
 *
 */
interface PersonDob {
    @JsonProperty
    Instant dob();
}
