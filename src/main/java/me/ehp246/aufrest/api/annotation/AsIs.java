package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates the JSON provider to bypass de-serialize text payload and return
 * the value as is.
 *
 * @author Lei Yang
 * @since 2.0
 * @version 2.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AsIs {

}
