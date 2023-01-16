package me.ehp246.aufrest.core.rest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;

/**
 * @author Lei Yang
 *
 */
@ByRest("${echo.base}/")
interface GetCase01 {
    /**
     * Should be getting from the type.
     */
    @OfRequest("get")
    void get();

    /**
     * Should be getting from the method.
     */
    @OfRequest("get1")
    void get(String str);

    @OfRequest
    void get(Integer i);

    default int getInc(int i) {
        return ++i;
    }
}
