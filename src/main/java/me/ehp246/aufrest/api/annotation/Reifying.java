package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Restores parameter types that are erased by Java compiler. Needed by the
 * framework and JSON provider to construct generic types.
 *
 * @author Lei Yang
 * @since 2.0
 * @version 2.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Reifying {
    Class<?>[] value();
}
