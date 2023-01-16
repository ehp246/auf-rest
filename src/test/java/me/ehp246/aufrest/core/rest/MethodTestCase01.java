package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@ByRest("")
interface MethodTestCase01 {
    void get();

    void getBySomething();

    void query(int i);

    @OfRequest
    void query();

    void post();

    void postByName();

    @OfRequest(method = HttpUtils.POST)
    void create();

    void delete();

    @OfRequest(method = HttpUtils.DELETE)
    void remove();

    void put();

    void patch();
}
