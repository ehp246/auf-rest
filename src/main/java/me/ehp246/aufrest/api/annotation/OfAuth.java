/**
 * 
 */
package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates the annotated parameter should be used as Authorization header. The
 * argument is converted to {@linkplain String} by
 * {@linkplain Object#toString()} and set as the value as-is.
 * <p>
 * The annotation has the highest priority on Authorization header. It
 * overwrites all other sources.
 * <p>
 * There is no validation on the value. Blank string will be accepted.
 * <p>
 * <code>null</code> is accepted and means no header.
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfAuth {
}
