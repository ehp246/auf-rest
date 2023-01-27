package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * Indicates that the parameter specifies the body object.
 * <p>
 * Most often the body argument can be inferred without the annotation.
 * <p>
 * Serializing by {@linkplain JsonView}.
 *
 * @author Lei Yang
 * @since 4.0
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfBody {
}
