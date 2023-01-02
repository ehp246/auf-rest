package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfPath {
    String value() default "";
}
