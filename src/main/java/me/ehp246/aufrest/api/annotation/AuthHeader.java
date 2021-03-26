/**
 * 
 */
package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates the annotated parameter should be used as Authorization header
 * value. The argument is converted to a String by Object::toString.
 * <p>
 * The annotation has the highest priority on Authorization header. It
 * overwrites all other sources.
 * <p>
 * If the argument is <code>null</code> or its toString value is
 * <cod>null</code> or blank, Authorization will not be set. It effectively
 * turns off Authorization for the invocation.
 * <p>
 * Non-null and non-blank String will be set as-is with no further processing.
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface AuthHeader {
}
