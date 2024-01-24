package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.exception.ErrorResponseException;
import me.ehp246.aufrest.api.rest.AuthScheme;

/**
 * Indicates that the annotated interface should be registered as a proxy of a
 * REST endpoint.
 * <p>
 * For each annotated interface, Auf REST defines a bean of the type and makes
 * it available for injection.
 * <p>
 * Body serialization and de-serialization by {@linkplain JsonView} are
 * supported .
 *
 * @author Lei Yang
 * @since 1.0
 * @see EnableByRest
 * @see OfBody
 * @version 4.0
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ByRest {

    /**
     * Defines the base URL of the REST endpoint that the interface proxies for.
     * <p>
     * The value specified is not validated until used. Simple path only without
     * parameters.
     * <p>
     * Supports Spring property placeholder (e.g. <code>"/${api.base}"</code>).
     *
     * @see <a href=
     *      "https://developer.mozilla.org/en-US/docs/Learn/Common_questions/What_is_a_URL#:~:text=With%20Hypertext%20and%20HTTP%2C%20URL,unique%20resource%20on%20the%20Web.">URL</a>
     */
    String value();

    /**
     * Defines an optional bean name by which the proxy interface can be injected.
     * <p>
     * The default is from {@link Class#getSimpleName()} with the first letter in
     * lower-case.
     *
     * @return the bean name of the proxy interface.
     * @see Qualifier
     */
    String name() default "";

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
     * For all un-recognized types, the content type is set to
     * <code>application/json</code> and body is serialized as JSON.
     */
    String contentType() default "";

    /**
     * Defines the Accept header. Usually derived by the return type of the method.
     */
    String accept() default "application/json";

    /**
     * Defines the Accept-Encoding request header value. When true, the default, the
     * header value will be set to 'gzip'. Otherwise, the header will not be set.
     */
    boolean acceptGZip() default true;

    /**
     * Specifies the Java type to which the response body of an
     * {@link ErrorResponseException} should be de-serialized to for the proxy
     * interface.
     */
    Class<?> errorType() default Object.class;

    /**
     * Defines the Authorization type and value required by the endpoint.
     * <p>
     * Note the default <code>Auth.scheme</code> for the element is
     * {@link me.ehp246.aufrest.api.rest.AuthScheme DEFAULT}. It is different from
     * an explicitly defined value which is set to
     * {@link me.ehp246.aufrest.api.rest.AuthScheme BEARER}.
     *
     * @see Auth
     */
    Auth auth() default @Auth(value = {}, scheme = AuthScheme.DEFAULT);

    /**
     * Defines request header names and values in pairs. E.g.,
     * <p>
     * <code>
     *     { "x-app-name", "AufRest", "x-app-version", "1.0", ... }
     * </code>
     * <p>
     * Must be specified in pairs. Missing value will trigger an exception. E.g.,
     * the following is missing value for header '{@code x-app-version}' and will
     * result an exception.
     * <p>
     * <code>
     *     { "x-app-name", "AufRest", "x-app-version" }
     * </code>
     * <p>
     * Header names are converted to lower case and can not be repeated. Values are
     * accepted as-is.
     * <p>
     * If the same header is defined by a {@linkplain OfHeader} parameter as well,
     * the parameter argument takes the precedence and is accepted. The value
     * defined here is ignored.
     * <p>
     * Spring property placeholder is supported on values but not on names.
     *
     */
    String[] headers() default {};

    /**
     * Defines queries in name and value pairs to be applied to all HTTP requests
     * from the interface.
     * <p>
     * Must be specified in pairs. Missing value will trigger an exception.
     * <p>
     * Both names and values are accepted as-is. I.e., they would be case-sensitive.
     * <p>
     * Parameter names can be repeated on the annotation. All the values on the
     * annotation are collected into a {@linkplain List} and applied to HTTP
     * requests.
     * <p>
     * If the same parameter name is specified by a {@linkplain OfQuery}, the
     * parameter argument takes the precedence and the annotation will be ignored.
     * <p>
     * Spring property placeholder is supported on values, not on names.
     *
     */
    String[] queries() default {};

    /**
     * Defines configuration of worker-thread
     * {@linkplain java.util.concurrent.Executor} for the {@linkplain HttpClient}.
     *
     * @see HttpClient.Builder#executor(java.util.concurrent.Executor)
     */
    Executor executor() default @Executor;

    /**
     * Defines the Authorization types supported.
     */
    @Target({})
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

    @Target({})
    @interface Executor {
        /**
         * Defines {@linkplain MDC}-map keys whose values should to be propagated to the
         * worker threads from the invoking thread. The values are retrieved at the time
         * of the invocation. If {@linkplain MDC} of the current thread doesn't have a
         * value for the key, <code>null</code> will be set as the value on the worker
         * thread's context.
         * <p>
         * If the same context key is defined both here and on a parameter, the
         * parameter argument has the precedence.
         */
        String[] mdc() default {};
    }
}
