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
 * AufRest does not validate the value. Blank string will be accepted.
 * <p>
 * <code>null</code> is accepted as no Authorization.
 * 
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface AuthHeader {
}
