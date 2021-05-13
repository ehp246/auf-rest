package me.ehp246.aufrest.api.spi;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 
 * @author Lei Yang
 * @since 2.2
 *
 */
public interface InvokedOn {
    Class<?> declaredType();

    Object target();

    Method method();

    List<?> args();
}
