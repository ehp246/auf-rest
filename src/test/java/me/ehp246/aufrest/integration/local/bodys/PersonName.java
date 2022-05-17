package me.ehp246.aufrest.integration.local.bodys;

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
