package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

import me.ehp246.aufrest.api.rest.HttpUtils;

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
	 */
	String contentType() default HttpUtils.APPLICATION_JSON;

	/**
	 * Defines the Accept header. Usually derived by the return type of the method.
	 */
	String accept() default HttpUtils.APPLICATION_JSON;

	/**
	 * Defines the Authorization type and value required by the endpoint.
	 *
	 * <p>
	 * Note the default <code>Auth.scheme</code> for the element is
	 * {@link me.ehp246.aufrest.api.annotation.ByRest.Auth.Scheme DEFAULT}. It is
	 * different from an explicitly defined value which is set to
	 * {@link me.ehp246.aufrest.api.annotation.ByRest.Auth.Scheme BEARER}.
	 *
	 * @see Auth
	 */
	Auth auth() default @Auth(args = "", scheme = Auth.Scheme.DEFAULT);

	/**
	 * Defines the Authorization types supported.
	 */
	@interface Auth {
		/**
		 * Defines the type of the Authorization required by the endpoint.
		 */
		Scheme scheme() default Scheme.BEARER;

		/**
		 * Defines the argument or arguments to construct Authorization header. See
		 * {@link Scheme Scheme} for how the provided values are used.
		 * <p>
		 * Spring property placeholder is supported.
		 * <p>
		 * In most cases, the framework does not validate any values provided by
		 * application. They are used as-is.
		 */
		String[] args() default "";

		/**
		 * Indicates to the framework how to construct the value of Authorization header
		 * for the endpoint with given scheme and arguments.
		 */
		enum Scheme {
			/**
			 * Indicates the value of Authorization header for the endpoint is to be
			 * provided by the optional global
			 * {@link me.ehp246.aufrest.api.rest.AuthProvider AuthProvider} bean. For this
			 * type, the args element is ignored.
			 * <p>
			 * The global bean is not defined by default. Additionally it could return
			 * <code>null</code> for the URI. In which case, the requests from the proxy
			 * interface will have no Authorization header.
			 *
			 * @see me.ehp246.aufrest.api.rest.AuthProvider
			 */
			DEFAULT,
			/**
			 * Indicates the endpoint requires HTTP basic authentication. For this scheme,
			 * the args element should specify the two components of user name and password
			 * in the format of <code>{"${username}", "${password}"}</code>. I.e., the first
			 * value is the username, the second the password.
			 * <p>
			 * Either component can be blank.
			 */
			BASIC,
			/**
			 * Indicates the endpoint requires Bearer token authorization. For this scheme,
			 * the args should be a single string that is the token without any prefix.
			 * <p>
			 * Blank string is accepted as-is. The framework does not validate the value.
			 * <p>
			 * Additional values are ignored.
			 * 
			 */
			BEARER,
			/**
			 * Indicates to the framework that the value should be set to the Authorization
			 * header as-is without any additional processing. This is mainly to provide a
			 * static direct access to the header.
			 * <p>
			 * Requires a single value. Only the first is accepted. Additional values are
			 * ignored.
			 * 
			 */
			SIMPLE,

			/**
			 * Indicates explicitly that Authorization should not be set.
			 */
			NONE
		}
	}

}
