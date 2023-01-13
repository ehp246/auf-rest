package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Parameter;

/**
 * Indicates that the parameter specifies a query parameter.
 * <p>
 * The following Java types are supported:
 * <p>
 * <li>String</li>
 * <p>
 * In this case, the query parameter name is from {@linkplain OfQuery#value()}
 * and it will have a single value.
 * <p>
 * <li>{@link java.util.List List&lt;String&gt;}</li>
 * <p>
 * In this case, the query parameter name is from {@linkplain OfQuery#value()}.
 * It will have a list of values.
 * <p>
 * <li>{@link java.util.Map Map&lt;String, String&gt;}</li>
 * <li>{@link java.util.Map Map&lt;String, List&lt;String, String&gt;&gt;}</li>
 * <p>
 * Map keys will be the query names and map values will query values.
 * {@linkplain OfQuery#value()} is ignored in this case.
 *
 * <p>
 * The values should not be encoded.
 * <p>
 * <code>null</code> value is not filtered.
 *
 * @author Lei Yang
 * @since 4.0
 */
@Retention(RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface OfQuery {
    /**
     * Defines the name of the query parameter.
     * <p>
     * If no value is provided, the return from {@linkplain Parameter#getName()}
     * will be used. To make this default behavior useful, Java compiler should
     * probably have <code>-parameters</code> turned on.
     * <p>
     * If the same query name is repeated on multiple parameters, all arguments will
     * be collected into a list under the name.
     *
     * @see <a href='https://openjdk.org/jeps/118'>JEP 118: Access to Parameter
     *      Names at Runtime</a>
     */
    String value() default "";
}
