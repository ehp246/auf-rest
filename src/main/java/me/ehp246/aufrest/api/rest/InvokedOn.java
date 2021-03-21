package me.ehp246.aufrest.api.rest;

import java.lang.reflect.Method;

/**
 * 
 * @author Lei Yang
 * @since 2.2
 *
 */
public interface InvokedOn {
	Object target();

	Method method();

	Object[] args();
}
