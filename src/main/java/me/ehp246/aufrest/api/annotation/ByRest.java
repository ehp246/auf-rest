package me.ehp246.aufrest.api.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.Duration;

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
@Documented
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
	 * Defines the Authorization type and value required by the endpoint.
	 *
	 * <p>
	 * Note the default <code>Auth.Type</code> for the element is
	 * {@link me.ehp246.aufrest.api.annotation.ByRest.Auth.Type DEFAULT}. It is
	 * different from an explicitly defined value which is set to
	 * {@link me.ehp246.aufrest.api.annotation.ByRest.Auth.Type BEARER}.
	 *
	 * @see Auth
	 */
	Auth auth() default @Auth(value = "", type = Auth.Type.DEFAULT);

	/**
	 * Defines the Authorization types supported.
	 */
	@interface Auth {
		/**
		 * Defines the value for the Authorization header. See {@link Type Type} for how
		 * the value is interpreted.
		 */
		String value() default "";

		/**
		 * Defines the type of the Authorization required by the endpoint.
		 */
		Type type() default Type.BEARER;

		/**
		 * Indicates to the framework how to retrieve the value of Authorization header
		 * for the endpoint.
		 */
		enum Type {
			/**
			 * Indicates the value of Authorization header for the endpoint is to be
			 * provided by the optional global
			 * {@link me.ehp246.aufrest.api.rest.AuthorizationProvider
			 * AuthorizationProvider} bean. For this type, the value element is ignored.
			 *
			 * <p>
			 * The global bean is not defined by default. Additionally it could return
			 * <code>null</code> for the URI. In which case, the requests from the proxy
			 * interface will have no Authorization header.
			 *
			 * @see me.ehp246.aufrest.api.rest.AuthorizationProvider
			 */
			DEFAULT,
			/**
			 * Indicates the endpoint requires HTTP basic authentication. For this type, the
			 * value element should be of the format of
			 * <code>"${username}:${password}"</code>. I.e., a simple concatenation of
			 * username, ":", and password with no additional encoding. The framework will
			 * encode the value according to the specification.
			 */
			BASIC,
			/**
			 * Indicates the endpoint requires Bearer token authorization. For this type,
			 * the value should be simple token string without any prefix.
			 */
			BEARER,
			/**
			 * Indicates to the framework that the value should be set to the Authorization
			 * header as-is without any additional processing. This is mainly to provide a
			 * static direct access to the header.
			 */
			ASIS,
			/**
			 * Indicates the endpoint requires for Authorization a value to be supplied by a
			 * Spring bean of the type {@link java.util.function.Supplier Supplier}. For
			 * this type, the value element is the bean name.
			 *
			 * <p>
			 * For each out-going request, the framework looks up for the bean of the name
			 * and type, retrieves the value object from the supplier, calls
			 * <code>Object::toString</code> on the value object, then sets Authorization
			 * header to the returned string as-is.
			 */
			BEAN
		}
	}

}
