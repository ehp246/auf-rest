package me.ehp246.aufrest.integration.local.returntype;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.rest.HttpUtils;

/**
 * @author Lei Yang
 *
 */
@ByRest("http://localhost:${local.server.port}/form/")
interface FormTestCase001 {
    @OfMapping(value = "person", contentType = HttpUtils.APPLICATION_FORM_URLENCODED, accept = HttpUtils.APPLICATION_FORM_URLENCODED)
    void post(String person);
}
