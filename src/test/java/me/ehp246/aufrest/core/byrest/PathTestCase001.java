package me.ehp246.aufrest.core.byrest;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;

import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * Test cases for path parameters
 *
 * @author Lei Yang
 *
 */
interface PathTestCase001 {
    @OfMapping("get/{path1}/path2/{path3}")
    void get(@PathVariable("path1") String path1, @PathVariable("path3") String path3);

    @OfMapping("{path3}/{path4}")
    void get(@PathVariable("path4") String path4, @PathVariable("path1") String path1,
            @PathVariable("path3") String path2);

    /**
     * This method should throw exception since PathParam's are missing.
     *
     */
    @OfMapping("get/{path1}/path2/{path3}")
    void get();

    /**
     * Map all path ids.
     *
     */
    @OfMapping("get/{path1}/path2/{path3}")
    void getByMap(@PathVariable Map<String, String> pathParams);

    /**
     * All path values are merged together. For the same path variable, explicit
     * parameter value takes precedence over the value, if there is one, from the
     * un-named map.
     *
     */
    @OfMapping("get/{path1}/path2/{path3}")
    void getByMap(@PathVariable Map<String, String> pathParams, @PathVariable("path1") String path1);
}
