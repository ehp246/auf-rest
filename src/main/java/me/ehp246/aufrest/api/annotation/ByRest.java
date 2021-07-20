package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * Indicates that the annotated interface should be scanned by the framework as
 * a proxy of a REST endpoint.
 * <p>
 * For each annotated interface, the framework defines a bean of the type and
 * makes it available for injection. On behalf of the application the framework
 * implements invocations on the interface as HTTP requests/responses.
 *
 * @author Lei Yang
 * @since 1.0
 * @see EnableByRest
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ByRest {

    /**
     * Full URL of the REST endpoint that the interface proxies. The element
     * supports Spring property placeholder (e.g. <code>"/${api.base}"</code>). The
     * value must resolve to a full HTTP-based URL.
     */
    String value();

    /**
     * Defines how long to wait for a response before raising a
     * {@link java.util.concurrent.TimeoutException TimeoutException}. The value is
     * parsed by {@link Duration java.time.Duration.parse(CharSequence)} and should
     * follow IS8601 Duration standard.
     * <p>
     * Spring property placeholder is supported.
     * <p>
     * The specified value overrides the global configuration for the interface.
     * Otherwise, the interface follows the global configuration.
     *
     * @see me.ehp246.aufrest.api.configuration.AufRestConstants
     */
    String timeout() default "";

    /**
     * Defines the content type and appropriate serialization provider.
     * <p>
     * The default, empty string, indicates to the framework to infer the
     * serialization strategy by the request body type.
     * <p>
     * For all un-recognized types, the contentType is set to application/json and
     * body is serialized as JSON.
     */
    String contentType() default "";

    /**
     * Defines the Accept header. Usually derived by the return type of the method.
     */
    String accept() default "application/json";

    /**
     * Defines the Accept-Encoding request header value. When true, the default, the
     * header value will be set to 'gzip'. Otherwise, the header will not be set.
     * 
     */
    boolean acceptGZip() default true;

    Class<?> errorType() default Default.class;

    /**
     * Defines the Authorization type and value required by the endpoint.
     *
     * <p>
     * Note the default <code>Auth.scheme</code> for the element is
     * {@link me.ehp246.aufrest.api.rest.AuthScheme DEFAULT}. It is
     * different from an explicitly defined value which is set to
     * {@link me.ehp246.aufrest.api.rest.AuthScheme BEARER}.
     *
     * @see Auth
     */
    Auth auth() default @Auth(value = {}, scheme = AuthScheme.DEFAULT);

    /**
     * Defines the Authorization types supported.
     */
    @interface Auth {
        /**
         * Defines the type of the Authorization required by the endpoint.
         */
        AuthScheme scheme() default AuthScheme.BEARER;

        /**
         * Defines the argument or arguments to construct Authorization header. See
         * {@link AuthScheme Scheme} for how the provided values are used.
         * <p>
         * Spring property placeholder is supported.
         * <p>
         * In most cases, the framework does not validate any values provided by
         * application. They are used as-is.
         */
        String[] value() default {};
    }

}
