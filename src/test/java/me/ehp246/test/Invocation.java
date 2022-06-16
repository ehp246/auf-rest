package me.ehp246.test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The abstraction of an invocation of a method.
 * 
 * @author Lei Yang
 * @since 1.0
 */
public interface Invocation {
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
