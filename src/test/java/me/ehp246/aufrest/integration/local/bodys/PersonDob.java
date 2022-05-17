package me.ehp246.aufrest.integration.local.bodys;

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
