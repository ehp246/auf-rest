package me.ehp246.aufrest.api.rest;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 * @since 2.1.1
 *
 */
public interface ProxyByRest {
	Object target();

	Method method();

	Object[] args();
}
