package me.ehp246.aufrest.api.rest;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface HeaderProvider {
	Map<String, List<String>> get(URI uri);
}
