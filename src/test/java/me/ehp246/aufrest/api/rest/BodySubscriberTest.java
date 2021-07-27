package me.ehp246.aufrest.api.rest;

import java.net.http.HttpClient.Version;
import java.net.http.HttpHeaders;
import java.net.http.HttpResponse.ResponseInfo;

import me.ehp246.aufrest.api.configuration.ByRestConfiguration;
import me.ehp246.aufrest.mock.Jackson;
import me.ehp246.aufrest.provider.jackson.JsonByJackson;

/**
 * @author Lei Yang
 *
 */
class BodySubscriberTest {
    private final JsonByJackson jacksonFn = new ByRestConfiguration().jacksonFn(Jackson.OBJECT_MAPPER);


    void errorType_001() {
        final var subscriber = new ByRestConfiguration().bodyHandlerProvider(jacksonFn).get(new RestRequest() {

            @Override
            public String uri() {
                // TODO Auto-generated method stub
                return null;
            }
        }).apply(new ResponseInfo() {

            @Override
            public Version version() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public int statusCode() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public HttpHeaders headers() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }
}
