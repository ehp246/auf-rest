package me.ehp246.aufrest.core.rest.requestbody;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Instant;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfResponse;

/**
 * @author Lei Yang
 *
 */
public interface BodyTestCases {
    @ByRest("")
    interface RequestCase01 {
        void get(BodyPublisher body);

        void get(InputStream body);

        void get(int i, BodyPublisher publisher, InputStream stream);

        void get(Instant now, int i);

        void get(Instant now, @OfBody String id);

        // Annotated has the highest priority
        void get(@OfBody String id, Instant now, BodyPublisher body);

        void getWithAuthParam(@AuthBean.Param String id);

        void getWithAuthParam(@AuthBean.Param String id, String body);
    }

    @ByRest("")
    interface ResponseCase01 {
        void getOnMethod(BodyHandler<?> handler);

        @OfRequest("")
        void getOfMapping();

        @OfRequest(value = "")
        @OfResponse(handler = "named")
        String getOfMappingNamed();
    }

    @ByRest(value = "")
    interface ResponseCase02 {
        void getOnMethod(int i, BodyHandler<?> handler);

        @OfRequest("")
        void get(BodyHandler<?> handler);

        @OfRequest("")
        void getOfMapping();

        @OfRequest(value = "")
        @OfResponse(handler = "methodNamed")
        String getOfMappingNamed();
    }
}
