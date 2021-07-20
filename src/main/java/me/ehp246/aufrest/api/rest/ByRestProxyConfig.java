package me.ehp246.aufrest.api.rest;

import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * Defines the configuration of a {@link ByRest} proxy. Mostly the Java object
 * equivalent of the annotation.
 * 
 * @author Lei Yang
 *
 */
public interface ByRestProxyConfig {
    String uri();

    default Auth auth() {
        return new Auth() {
        };
    }

    String timeout();

    String accept();

    String contentType();

    default boolean acceptGZip() {
        return true;
    }

    default Class<?> errorType() {
        return Object.class;
    }

    interface Auth {
        default List<String> value() {
            return List.of();
        }

        default AuthScheme scheme() {
            return AuthScheme.DEFAULT;
        }
    }
}
