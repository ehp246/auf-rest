package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.configuration.AufRestConfiguration;
import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.core.byrest.ByRestFactory;
import me.ehp246.aufrest.core.byrest.ByRestRegistrar;

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
@Import({ ByRestRegistrar.class, AufRestConfiguration.class, ByRestFactory.class })
public @interface EnableByRest {
    /**
     * Specifies the packages to scan for annotated
     * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces. The
     * package of each class specified will be scanned.
     */
    Class<?>[] scan() default {};

    /**
     * Specifies the Java type to which the response body of an
     * {@link ErrorResponseException} should be de-serialized to.
     * <p>
     * This is the global value that applies to all {@link ByRest} instances where
     * the errorType is {@link Default}.
     * 
     */
    Class<?> errorType() default Object.class;
}
