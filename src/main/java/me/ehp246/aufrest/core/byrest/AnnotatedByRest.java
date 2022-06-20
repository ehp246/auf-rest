package me.ehp246.aufrest.core.byrest;

import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.rest.AuthScheme;
import me.ehp246.aufrest.core.byrest.AnnotatedByRest.AuthConfig;


/**
 * A simple carrier for the values of {@linkplain ByRest}. Mostly a direct
 * mapping from an annotation with minimum processing.
 * 
 * @author Lei Yang
 */
public record AnnotatedByRest(String uri, AuthConfig auth, String timeout, String accept, String contentType,
        boolean acceptGZip, Class<?> errorType, String responseBodyHandler) {

    public record AuthConfig(List<String> value, AuthScheme scheme) {
        public AuthConfig() {
            this(List.of(), AuthScheme.DEFAULT);
        }
    }
}
