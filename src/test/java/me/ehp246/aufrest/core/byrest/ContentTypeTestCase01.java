/**
 * 
 */
package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
interface ContentTypeTestCase01 {
    // Should go with the interface
    void get1();

    // Should go with the method
    @OfMapping
    void get2();

    // Should go with the method
    @OfMapping(contentType = "m-type", accept = "m-accept")
    void get3();
}
