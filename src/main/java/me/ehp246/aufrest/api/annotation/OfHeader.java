package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When applied on a {@linkplain ByRest} method, it specifies the values of the
 * named header from the response should be returned. The following types are
 * supported:
 * <li>String</li>
 * <li>List</li>
 * <li>Map</li>
 *
 * <p>
 * If applied to a method, the response body will be discarded.
 *
 *
 * @author Lei Yang
 * @since 4.0
 */
@Retention(RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
public @interface OfHeader {
    /**
     * The name of the header.
     *
     */
    String value() default "";
}
