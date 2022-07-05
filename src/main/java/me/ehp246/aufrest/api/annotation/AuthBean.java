package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * Indicates the Authorization header should be provided by a bean as specified
 * via {@linkplain AuthScheme#BEAN}.
 * 
 * @author Lei Yang
 * @see ByRest#auth
 */
@Retention(RUNTIME)
@Target({})
public @interface AuthBean {

    /**
     * Indicates the argument of the parameter should be passed to the bean.
     * <p>
     * The annotation is to be applied on a method parameter of a
     * {@linkplain ByRest} interface.
     */
    @Retention(RUNTIME)
    @Target(PARAMETER)
    @interface Param {
    }

    /**
     * Indicates which method to invoke.
     * <p>
     * The annotation is to be applied to the bean that provides the Authorization
     * header.
     */
    @Retention(RUNTIME)
    @Target(ElementType.METHOD)
    @interface Invoking {
        String value() default "";
    }
}
