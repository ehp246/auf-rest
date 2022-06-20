package me.ehp246.aufrest.api.configuration;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.core.byrest.AufRestConfiguration;
import me.ehp246.aufrest.core.byrest.ByRestProxyFactory;
import me.ehp246.aufrest.core.byrest.ByRestRegistrar;
import me.ehp246.aufrest.core.byrest.DefaultProxyMethodParser;

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
@Import({ ByRestRegistrar.class, AufRestConfiguration.class, ByRestProxyFactory.class, DefaultProxyMethodParser.class,
        ByRestProxyFactory.class })
public @interface EnableByRest {
    /**
     * Specifies the packages to scan for annotated
     * {@link me.ehp246.aufrest.api.annotation.ByRest ByRest} interfaces. The
     * package of each class specified will be scanned.
     */
    Class<?>[] scan() default {};
}