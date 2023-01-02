package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When used on a parameter of a {@linkplain ByRest} method, it specifies the
 * body object for the out-going HTTP request.
 * <p>
 * When used on a {@linkplain ByRest} method, it specifies the full type
 * including the type parameters needed to de-serialize response body.
 *
 * @author Lei Yang
 */
@Retention(RUNTIME)
@Target({ METHOD, ElementType.PARAMETER })
public @interface OfBody {
    Class<?>[] value() default {};
}
