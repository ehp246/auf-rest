package me.ehp246.aufrest.api.rest;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author Lei Yang
 * @since 2.2
 *
 */
public interface Invocation {
    Class<?> declaredType();

    Object target();

    Method method();

    List<?> args();
}
