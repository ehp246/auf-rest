package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.configuration.ByRestConfiguration;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.core.byrest.EnableByRestRegistrar;

/**
 * Enables AufRest's annotation-driven REST-proxing capability for client-side
 * applications. It imports infrastructure beans and scans the class path for
 * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest}-annotated interfaces
 * making them available to be injected as Spring beans.
 *
 * @author Lei Yang
 * @since 1.0
 * @see ByRest
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({ EnableByRestRegistrar.class, ByRestConfiguration.class, ByRestFactory.class })
public @interface EnableByRest {
    /**
     * Specifies the packages to scan for annotated
     * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces. The
     * package of each class specified will be scanned.
     */
    Class<?>[] scan() default {};

    Class<?> errorType() default Object.class;
}
