package me.ehp246.aufrest.api.rest;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * Indicates to the framework how to construct the value of Authorization header
 * for the endpoint with given scheme and arguments.
 * 
 * @author Lei Yang
 * @since 1.0
 * @see ByRest
 */
public enum AuthScheme {
    /**
     * Indicates the value of Authorization header for the endpoint is to be
     * provided by the optional global
     * {@link me.ehp246.aufrest.api.rest.AuthProvider AuthProvider} bean. For this
     * type, the value element is ignored.
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
     * the value element should specify the two components of user name and password
     * in the format of <code>{"${username}", "${password}"}</code>. I.e., the first
     * value is the username, the second the password.
     * <p>
     * Either component can be blank.
     */
    BASIC,
    /**
     * Indicates the endpoint requires Bearer token authorization. For this scheme,
     * the value should be a single string that is the token without any prefix.
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
     * Indicates the value of Authorization header for the endpoint is to be
     * provided by the named Spring bean and its method.
     * <p>
     * The first value defines the bean name used for lookup.
     * <p>
     * The second value defines the method name. The method must be {@code public},
     * its parameter signature must match {@linkplain AuthBeanParam}-annotated
     * parameters exactly. Its return type must be {@linkplain String}.
     * <p>
     * If no bean as such can be found at invocation,
     * {@link NoSuchBeanDefinitionException} will be thrown.
     */
    BEAN,

    /**
     * Indicates explicitly that Authorization should not be set.
     * <p>
     * This value suppresses all other configuration.
     */
    NONE
}