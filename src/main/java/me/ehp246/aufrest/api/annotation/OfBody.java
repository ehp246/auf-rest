package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the parameter specifies the body object.
 * <p>
 * Most often the body object can be inferred without the annotation.
 *
 * @author Lei Yang
 * @since 4.0
 * 
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfBody {
}
