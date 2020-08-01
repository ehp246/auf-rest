package org.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target({ METHOD })
public @interface OfMapping {
	String value() default "";

	RequestMethod method() default RequestMethod.GET;
}
