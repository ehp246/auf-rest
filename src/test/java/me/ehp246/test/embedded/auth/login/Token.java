package me.ehp246.test.embedded.auth.login;

import java.time.Instant;

/**
 * The public is needed for reflection access in unit tests.
 *
 * @author Lei Yang
 *
 */
public record Token(String token, Instant expiry) {
}
