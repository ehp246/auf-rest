package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InvocationAuthProvider;

/**
 * Instructs the framework on how to construct HTTP requests from method
 * invocations.
 *
 * <p>
 * It is to be applied to methods on
 * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces.
 *
 * @author Lei Yang
 * @since 1.0
 * @see ByRest
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface OfMapping {
    /**
     * Defines additional path to append to the URL specified by
     * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} on the interface.
     *
     * <p>
     * Spring property place-holders are supported. Simple string concatenation to
     * construct the final URL.
     */
    String value() default "";

    /**
     * Defines HTTP method for the request.
     * <p>
     * Empty string indicates to derive HTTP method from interface method prefix.
     * <p>
     * Specified string is changed to upper case before sent. There is no validation
     * on the specified value.
     */
    String method() default "";

    /**
     * Defines the content type and appropriate serialization provider.
     */
    String contentType() default "";

    /**
     * Defines the Accept header. Usually derived by the return type of the method.
     */
    String accept() default HttpUtils.APPLICATION_JSON;

    /**
     * Defines the name of a Spring bean of {@link InvocationAuthProvider} type that
     * would provide the Authorization header for the invocations on the method.
     * <p>
     * Empty string indicates there is no bean for the method.
     * <p>
     * When a name is specified but no bean of the type can be found at invocation,
     * {@link NoSuchBeanDefinitionException} will be thrown.
     * 
     * @return a bean name
     */
    String authProvider() default "";
}
