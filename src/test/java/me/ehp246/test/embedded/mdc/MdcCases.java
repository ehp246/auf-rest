package me.ehp246.test.embedded.mdc;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Executor;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfResponse;

/**
 * @author Lei Yang
 *
 */
interface MdcCases {
    @ByRest(value = "http://localhost:${local.server.port}/mdc",
            executor = @Executor(mdc = { "logger_context_1", "logger_context_2" }))
    interface Case01 {
        Order post(@OfHeader("AccountId") final String accountId, Order order);

        @OfResponse(handler = "responseHandler1")
        void postWithVoidHandler(@OfHeader final String accountId, Order order);

        @OfResponse(handler = "responseHandler1")
        void postWithVoidHandlerWithIntrospect(@OfHeader final String accountId, Order order);
    }

    record Order(String orderId, int amount) {
    }
}