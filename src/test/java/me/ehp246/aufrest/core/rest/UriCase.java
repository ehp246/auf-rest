package me.ehp246.aufrest.core.rest;

import java.util.Map;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.annotation.OfPath;

/**
 * Test cases for path parameters
 *
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/")
interface UriCase {
    @OfRequest
    void get001();

    @OfRequest("get/{path1}/path2/{path3}")
    void getByPathVariable(@OfPath("path1") String path1, @OfPath("path3") String path3);

    @OfRequest("{path3}/{path4}")
    void getByPathParam(@OfPath("path4") String path4, @OfPath("path1") String path1,
            @OfPath("path3") String path2);

    /**
     * Map all path ids.
     *
     * @param pathParams
     * @return
     */
    @OfRequest("get/{path1}/path2/{path3}")
    void getByMap(@OfPath Map<String, String> pathParams);

    /**
     * All path values are merged together. For the same path variable, explicit
     * parameter value takes precedence over the value, if there is one, from the
     * un-named map.
     *
     * @param pathParams
     * @param path1
     * @return
     */
    @OfRequest("get/{path1}/path2/{path3}")
    void getByMap(@OfPath Map<String, String> pathParams, @OfPath("path1") String path1);

    @OfRequest("get/{path1}/path2/{path3}")
    void getByObject(@OfPath PathObject pathObject);

    /**
     * This method should throw exception since PathParam's are missing.
     *
     * @return
     */
    @OfRequest("get/{path1}/path2/{path3}")
    void get();

    @OfRequest("get")
    void getWithPlaceholder();

    interface PathObject {
        String getPath1();

        String getPath3();
    }
}
