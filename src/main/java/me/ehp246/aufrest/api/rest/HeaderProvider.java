package me.ehp246.aufrest.api.rest;

import java.util.List;
import java.util.Map;

/**
 * Defines the type of a Spring bean that applies to the global scope as header
 * provider for {@link me.ehp246.aufrest.api.annotation.ByRest ByRest}
 * interfaces.
 *
 * <p>
 * The framework calls the bean passing in the URI of the endpoint to retrieve
 * header names and values for all out-going requests. The framework calls the
 * bean once for each request. It does not cache any value. The framework does
 * not promise to pass in the same URI object for the same interface across
 * requests. It does promise to not pass in <code>null</code>.
 *
 * <p>
 * For a given URI, the provider should return the header names and values. The
 * returned values are set to the headers as-is with no additional processing
 * unless the value is null, blank, or empty. In these cases, the value will not
 * be set.
 *
 *
 * @author Lei Yang
 * @since 1.1
 */
@FunctionalInterface
public interface HeaderProvider {
	/**
	 *
	 * @param uri the target URI of the request
	 * @return header names and values. <code>null</code> is acceptable.
	 */
	Map<String, List<String>> get(String uri);
}
