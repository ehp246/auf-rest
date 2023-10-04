package me.ehp246.test.embedded.log4jcontext;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfLog4jContext;

/**
 * @author Lei Yang
 *
 */
interface Log4jContextrCases {
    @ByRest("http://localhost:${local.server.port}/log4jcontext")
    interface Case01 {
        Order post(@OfHeader("AccountId") @OfLog4jContext final String accountId, Order order);
    }

    record Order(@OfLog4jContext String orderId, int amount) {
    }
}