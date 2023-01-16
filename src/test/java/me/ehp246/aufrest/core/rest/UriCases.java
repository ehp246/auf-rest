package me.ehp246.aufrest.core.rest;

import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfPath;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * Test cases for path parameters
 *
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/")
interface UriCases {
    @ByRest("${echo.base}/")
    interface Uri01 {
        @OfRequest
        void get();

        @OfRequest("get")
        void getWithSub();

        @OfRequest("get/{path1}/path2/{path3}")
        void getByPathVariable(@OfPath("path1") String path1, @OfPath("path3") String path3);

        @OfRequest("get/{path1}/path2/{path3}")
        void getByMap(@OfPath Map<String, String> pathParams);

        /**
         * All path values are merged together. For the same path variable, explicit
         * parameter value takes precedence over the value, if there is one, from the
         * un-named map.
         *
         */
        @OfRequest("get/{path1}/path2/{path3}")
        void getByMap(@OfPath Map<String, String> pathParams, @OfPath("path1") String path1);
    }

    @ByRest("${echo.base}/{root}/{id}")
    interface Uri02 {
        void get(@OfPath String root, @OfPath String id);
    }
}
