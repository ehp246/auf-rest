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
 * <ul>
 * <li>{@link java.util.List List&lt;?&gt;}
 * <p>
 * The header will have all the values.
 * <li>{@link java.util.Map Map&lt;String, ?&gt;}
 * <p>
 * Map keys will become header names and map values header values.
 * {@linkplain OfHeader#value()} is ignored in this case.
 * <li>{@linkplain Object}
 * <p>
 * {@linkplain Object#toString()} will be called for the text value.
 * </ul>
 * <p>
 * <code>null</code> encountered will be skipped.
 *
 * @author Lei Yang
 * @since 4.0
 * @see <a href='https://openjdk.org/jeps/118'>JEP 118: Access to Parameter
 *      Names at Runtime</a>
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
     */
    String value() default "";
}
