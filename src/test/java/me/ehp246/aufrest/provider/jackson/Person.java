package me.ehp246.aufrest.provider.jackson;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
class Person extends PersonName {
    @JsonView(RestView.class)
    private final Instant dob;
    @JsonView({ String.class })
    private final String prefix;

    public Person(final Instant dob, final String firstName, final String lastName) {
        super(firstName, lastName);
        this.dob = dob;
        this.prefix = null;
    }

    public Person(final Instant dob, final String prefix, final String firstName, final String lastName) {
        super(firstName, lastName);
        this.dob = dob;
        this.prefix = prefix;
    }

    public Instant getDob() {
        return dob;
    }
}
