package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufrest.api.rest.HttpUtils;

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
 * @version 2.0.1
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
	 */
	String method() default HttpUtils.GET;

	/**
	 * Defines the content type and appropriate serialization provider.
	 */
	String produces() default HttpUtils.APPLICATION_JSON;

	/**
	 * Defines the Accept header. Usually derived by the return type of the method.
	 */
	String consumes() default "";
}
