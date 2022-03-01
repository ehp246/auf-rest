/**
 * 
 */
package me.ehp246.aufrest.core.byrest;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;

/**
 * @author Lei Yang
 *
 */
interface ContentTypeTestCases {
    @ByRest(value = "", contentType = "i-type", accept = "i-accept")
    interface Case001 {
        // Should go with the interface
        void get1();

        // Should go with the interface
        @OfMapping
        void get2();

        // Should go with the method
        @OfMapping(contentType = "m-type", accept = "m-accept")
        void get3();
    }

    @ByRest(value = "", contentType = "i-type")
    interface Case002 {
        // Should go with the interface
        void get1();

        // Should go with the interface
        @OfMapping
        void get2();

        // Should go with the method
        @OfMapping(contentType = "m-type", accept = "m-accept")
        void get3();
    }
}
