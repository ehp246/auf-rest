package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * Indicates Authorization header should be provided by a bean as specified via
 * {@linkplain AuthScheme#BEAN}.
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
     * Indicates the method is part of a {@linkplain AuthScheme#BEAN}.
     * <p>
     * The annotation is to be applied to the bean's method that provides the
     * Authorization header.
     * <p>
     * The return value from the method is converted to {@linkplain String} via
     * {@linkplain Object#toString()}. <code>null</code> means no
     * <code>Authorization</code> header.
     * <p>
     * If the method throws a {@linkplain RuntimeException} during invocation, the
     * exception will be propagated as-is. If the thrown is a checked exception, it
     * will be wrapped in a {@linkplain RuntimeException} first.
     */
    @Retention(RUNTIME)
    @Target(ElementType.METHOD)
    @interface Invoking {
        /**
         * Specifies the name for method lookup. This value should match the second
         * value for {@linkplain AuthScheme#BEAN} scheme on {@linkplain ByRest#auth()}
         * <p>
         * The default is to use the declared method name.
         */
        String value() default "";
    }
}
