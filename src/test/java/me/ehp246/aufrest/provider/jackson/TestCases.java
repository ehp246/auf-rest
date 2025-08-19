package me.ehp246.aufrest.provider.jackson;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
class TestCases {
    public static interface Person01 {
        Instant getDob();

        @JsonView(RestView.class)
        String getFirstName();

        @JsonView(RestView.class)
        String getLastName();
    }
}
