package me.ehp246.aufrest.core.byrest.requestbody;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandler;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
interface BodyTestCases {
    @ByRest("")
    interface RequestCase01 {
        void get(BodyPublisher body);

        void get(InputStream body);

        void get(int i, BodyPublisher publisher, InputStream stream);
    }

    @ByRest("")
    interface ResponseCase01 {
        void getOnMethod(BodyHandler<?> handler);

        @OfMapping("")
        void getOfMapping(BodyHandler<?> handler);

        @OfMapping("")
        void getOfMapping();

        @OfMapping(value = "", bodyHandlerProvider = "named")
        String getOfMappingNamed();
    }
    
    @ByRest(value = "", bodyHandlerProvider = "interfaceNamed")
    interface ResponseCase02 {
        void getOnMethod(int i, BodyHandler<?> handler);

        @OfMapping("")
        void getOfMapping(BodyHandler<?> handler);

        @OfMapping("")
        void getOfMapping();

        @OfMapping(value = "", bodyHandlerProvider = "methodNamed")
        String getOfMappingNamed();
    }
}
