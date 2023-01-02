package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to define the typing information of the body.
 * <p>
 * When used on a {@linkplain ByRest} method, it specifies the full type
 * including the type parameters that are erased by Java compiler but needed to
 * re-construct generic types.
 *
 * @author Lei Yang
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface OfBody {
    Class<?>[] value();
}
