package me.ehp246.aufrest.api.spi;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The abstraction of an invocation of a method.
 * 
 * @author Lei Yang
 * @since 2.2
 *
 */
public interface Invocation {
    /**
     * The type where the method is declared.
     * 
     */
    Class<?> declaredType();

    /**
     * The object on which the invocation is made.
     * 
     */
    Object target();

    /**
     * The method that is invoked.
     * 
     */
    Method method();

    /**
     * Arguments of the invocation.
     * 
     */
    List<?> args();
}
