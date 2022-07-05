package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({})
public @interface AuthBean {

    @Retention(RUNTIME)
    @Target(PARAMETER)
    @interface Param {
    }

    @Retention(RUNTIME)
    @Target(ElementType.METHOD)
    @interface Invoking {
        String value() default "";
    }
}
