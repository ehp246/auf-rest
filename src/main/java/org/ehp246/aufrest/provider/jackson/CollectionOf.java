package org.ehp246.aufrest.provider.jackson;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
/**
 * @author Lei Yang
 *
 */
public @interface CollectionOf {
	Class<?>[] value();
}
