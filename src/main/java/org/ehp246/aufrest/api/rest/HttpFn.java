package org.ehp246.aufrest.api.rest;

import java.util.function.Supplier;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface HttpFn {
	Supplier<Response> apply(Request request);
}
