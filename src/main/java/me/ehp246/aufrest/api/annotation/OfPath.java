package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

/**
 * Indicates that the parameter specifies the value of a path variable.
 * <p>
 * Argument will be converted to {@linkplain String} by
 * {@linkplain Object#toString()}.
 * <p>
 * String value should NOT be encoded.
 *
 * @author Lei Yang
 * @since 4.0
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfPath {
    /**
     * Defines the name of the header.
     * <p>
     * If no value is provided, the return from {@linkplain Parameter#getName()}
     * will be used. To make this default behavior useful, Java compiler should
     * probably have <code>-parameters</code> turned on.
     * <p>
     * Repeated header from multiple parameters will be collected into a list.
     *
     * @see <a href='https://openjdk.org/jeps/118'>JEP 118: Access to Parameter
     *      Names at Runtime</a>
     */
    String value() default "";
}
