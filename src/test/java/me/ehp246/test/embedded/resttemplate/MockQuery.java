package me.ehp246.test.embedded.resttemplate;

import java.util.List;
import java.util.Map;

import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
class MockQuery implements RestRequest {
    public String uri;
    public Map<String, List<String>> queries;

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public Map<String, List<String>> queries() {
        return queries;
    }
}
