package me.ehp246.aufrest.core.byrest.requestbody;

import java.io.InputStream;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodyHandler;
import java.time.Instant;

import org.springframework.web.bind.annotation.RequestBody;

import me.ehp246.aufrest.api.annotation.AuthBean;
import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

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

        void get(Instant now, @RequestBody String id);

        // Annotated has the highest priority
        void get(@RequestBody String id, Instant now, BodyPublisher body);

        void getWithAuthParam(@AuthBean.Param String id);

        void getWithAuthParam(@AuthBean.Param String id, String body);
    }

    @ByRest("")
    interface ResponseCase01 {
        void getOnMethod(BodyHandler<?> handler);

        @OfMapping("")
        void getOfMapping();

        @OfMapping(value = "", responseBodyHandler = "named")
        String getOfMappingNamed();
    }
    
    @ByRest(value = "", responseBodyHandler = "interfaceNamed")
    interface ResponseCase02 {
        void getOnMethod(int i, BodyHandler<?> handler);

        @OfMapping("")
        void get(BodyHandler<?> handler);

        @OfMapping("")
        void getOfMapping();

        @OfMapping(value = "", responseBodyHandler = "methodNamed")
        String getOfMappingNamed();
    }
}
