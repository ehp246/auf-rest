package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

/**
 * Indicates that the annotated parameter specifies a header value.
 * <p>
 * The following Java types are supported:
 * <p>
 * <li>{@link java.util.List List&lt;?&gt;}</li>
 * <p>
 * The header will have all the values.
 * <p>
 * <li>{@link java.util.Map Map&lt;String, ?&gt;}</li>
 * <p>
 * Map keys will become header names and map values header values.
 * {@linkplain OfHeader#value()} is ignored in this case.
 * <p>
 * <li>Object</li>
 * <p>
 * {@linkplain Object#toString()} will be called for the text value.
 * <p>
 * <code>null</code> encountered will be skipped.
 * <p>
 *
 * @author Lei Yang
 * @since 4.0
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfHeader {
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
