package me.ehp246.test.embedded.log4jcontext;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Executor;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfResponse;

/**
 * @author Lei Yang
 *
 */
interface Log4jContextrCases {
    @ByRest(value = "http://localhost:${local.server.port}/log4jcontext", executor = @Executor(log4jContext = {
            "logger_context_1", "logger_context_2" }))
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