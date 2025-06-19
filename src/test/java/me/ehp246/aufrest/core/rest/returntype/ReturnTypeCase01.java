package me.ehp246.aufrest.core.rest.returntype;

import java.net.http.HttpResponse;
import java.util.List;

import me.ehp246.aufrest.api.annotation.ByRest;

/**
 * @author Lei Yang
 *
 */
@SuppressWarnings("rawtypes")
@ByRest("")
interface ReturnTypeCase01 {
    HttpResponse get01();

    List get02();
}
