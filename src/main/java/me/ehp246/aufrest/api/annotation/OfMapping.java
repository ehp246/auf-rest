package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface OfMapping {
	String value() default "";

	String method() default HttpUtils.GET;

	String produces() default HttpUtils.APPLICATION_JSON;

}
