package me.ehp246.aufrest.integration.model;

import java.time.Instant;

/**
 * @author Lei Yang
 *
 */
public interface Person {
	Instant getDob();

	default String getName() {
		return null;
	}
}
