/**
 *
 */
package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates the parameter should be used as <code>Authorization</code> header
 * value. Argument is converted to {@linkplain String} by
 * {@linkplain Object#toString()} and set as the value as-is.
 * <p>
 * The annotation has the highest priority on the header. It overwrites all
 * other sources including {@linkplain OfHeader}.
 * <p>
 * There is no validation on the value. Blank string will be accepted.
 * <p>
 * <code>null</code> means no header.
 *
 * @author Lei Yang
 * @since 4.0
 * @version 4.0
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface OfAuth {
}
