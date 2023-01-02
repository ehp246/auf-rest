package me.ehp246.aufrest.core.rest;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
class QueryParamCases {
    @ByRest("${echo.base}/get")
    interface QueryParamCase01 {
        @OfMapping(method = "GET")
        void queryByParams(@RequestParam("query1") String query1, @RequestParam("query2") String query2);

        @OfMapping(method = "GET")
        void queryEncoded(@RequestParam("query 1") String query1);

        void getByMap(@RequestParam Map<String, String> queryParams);

        void getByMap(@RequestParam Map<String, String> queryParams, @RequestParam("query2") String query2);

        void getByMultiple(@RequestParam("query 1") String query1, @RequestParam("query 1") String query2);

        void getByList(@RequestParam("qList") List<String> q);
    }

    @ByRest(value = "${echo.base}/get", queries = { "query2", "${api.bearer.token}", "query3",
            "08dda6c5-e80f-44ef-b0cb-d9c261bf8352", "query3", "08dda6c5-e80f-44ef-b0cb-d9c261bf8353" })
    interface Case02 {
        void get();

        void getByParams(@RequestParam("query1") String query1);

        void getByMap(@RequestParam Map<String, String> queryParams);
    }

    /**
     * Failures.
     */
    @ByRest(value = "${echo.base}/get", queries = { "query2", "${api.bearer.token}", "query3" })
    interface Case03 {
        void get();
    }
}
