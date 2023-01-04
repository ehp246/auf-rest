package me.ehp246.test.local.body;

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
