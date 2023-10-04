package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.logging.log4j.ThreadContext;

/**
 * Specifies a supplier for a Log4j {@linkplain ThreadContext} value on a
 * {@linkplain ByRest} interface.
 * <p>
 * When applied to a parameter, the context value will be supplied by the
 * argument via {@linkplain Object#toString()}.
 * <p>
 * The annotation can also be applied to a supplier method defined by the type
 * of the body parameter.
 *
 * The supplier method must
 * <ul>
 * <li>be declared on the type directly, no recursion or inheritance</li>
 * <li>be <code>public</code></li>
 * <li>have no parameter</li>
 * <li>return a value</li>
 * </ul>
 * The return value will be converted to {@linkplain String} via
 * {@linkplain Object#toString()}. If no name is specified by the annotation,
 * the method name will be used as the prefix of the context key.
 * <p>
 * <code>null</code> will be converted to string <code>"null"</code>.
 *
 * @author Lei Yang
 * @since 4.1.2
 * @see ThreadContext
 * @see Parameter#getName()
 * @see Method#getName()
 * @see <a href='https://openjdk.org/jeps/118'>JEP 118: Access to Parameter
 *      Names at Runtime</a>
 * @see <a href=
 *      'https://logging.apache.org/log4j/2.x/manual/thread-context.html'>Log4j
 *      2 Thread Context</a>
 *
 */
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER })
public @interface OfLog4jContext {
    /**
     * Specifies the name of the {@linkplain ThreadContext}.
     * <p>
     * When no value is specified, the context name is inferred from the
     * parameter/method name. For this to work properly, '<code>-parameters</code>'
     * compiler option is desired.
     */
    String value() default "";
}
