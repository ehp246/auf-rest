package org.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface EnableByRest {
	Class<?>[] scan() default {};
}
