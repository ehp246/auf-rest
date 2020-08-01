package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ByRest {
	String value();

	/**
	 * Request timeout in milli-seconds. Defaults to no timeout.
	 *
	 * @return
	 */
	long timeout()

	default 0;

	Auth auth() default @Auth(value = "", type = Auth.Type.DEFAULT);

	@interface Auth {
		String value() default "";

		Type type() default Type.BEARER;

		enum Type {
			DEFAULT, BASIC, BEARER, ASIS, BEAN
		}
	}

}
