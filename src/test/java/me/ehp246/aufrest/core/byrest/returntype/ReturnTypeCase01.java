package me.ehp246.aufrest.core.byrest.returntype;

import java.net.http.HttpResponse;
import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("")
interface ReturnTypeCase01 {
    // Should throw
    HttpResponse get01();

    // Should throw
    HttpResponse<EchoResponseBody> get02();

    interface EchoResponseBody {
        Map<String, String> getArgs();

        Map<String, String> getHeaders();

        String getUrl();

        String getData();

        String getJson();
    }
}
