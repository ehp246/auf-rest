package me.ehp246.aufrest.core.byrest;

import java.lang.reflect.Method;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ProxyMethodParser {
    InvocationRequestBinder parse(Method method);
}
