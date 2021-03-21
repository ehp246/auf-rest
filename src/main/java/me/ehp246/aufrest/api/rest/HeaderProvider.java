package me.ehp246.aufrest.api.rest;

import java.util.List;
import java.util.Map;

/**
 * Defines the type of a Spring bean that applies to the global scope as header
 * provider for {@link me.ehp246.aufrest.api.annotation.ByRest ByRest}
 * interfaces.
 *
 * <p>
 * The framework calls the bean passing in the out-going request to retrieve
 * header names and values. The framework calls the bean once for each request.
 * It does not cache any value.
 *
 * <p>
 * The returned values are set to the headers as-is with no additional
 * processing unless the value is null, blank, or empty. In these cases, the
 * header will not be modified.
 *
 *
 * @author Lei Yang
 * @since 2.0
 */
@FunctionalInterface
public interface HeaderProvider {
	/**
	 *
	 * @param request the out-going request.
	 * @return header names and values. <code>null</code> is acceptable.
	 */
	Map<String, List<String>> get(RequestByRest request);
}
