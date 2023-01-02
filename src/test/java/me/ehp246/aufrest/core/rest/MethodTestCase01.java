package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
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

    @OfMapping
    void query();

    void post();

    void postByName();

    @OfMapping(method = HttpUtils.POST)
    void create();

    void delete();

    @OfMapping(method = HttpUtils.DELETE)
    void remove();

    void put();

    void patch();
}
