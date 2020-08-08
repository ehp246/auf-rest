package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.configuration.ByRestConfiguration;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.core.byrest.ByRestRegistrar;

/**
 * @author Lei Yang
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ ByRestRegistrar.class, ByRestConfiguration.class, ByRestFactory.class })
public @interface EnableByRest {
	Class<?>[] scan() default {};
}
