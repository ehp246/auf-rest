package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/")
interface GetCase01 {
    /**
     * Should be getting from the type.
     */
    @OfMapping("get")
    void get();

    /**
     * Should be getting from the method.
     */
    @OfMapping("get1")
    void get(String str);

    @OfMapping
    void get(Integer i);

    default int getInc(int i) {
        return ++i;
    }
}
