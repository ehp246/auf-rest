package me.ehp246.test.local.body;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lei Yang
 *
 */
interface PersonName {
    @JsonProperty
    String firstName();

    @JsonProperty
    String lastName();
}
