package me.ehp246.test.embedded.log4jcontext;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.ByRest.Executor;
import me.ehp246.aufrest.api.annotation.OfHeader;
import me.ehp246.aufrest.api.annotation.OfLog4jContext;
import me.ehp246.aufrest.api.annotation.OfResponse;

/**
 * @author Lei Yang
 *
 */
interface Log4jContextrCases {
    @ByRest(value = "http://localhost:${local.server.port}/log4jcontext", executor = @Executor(log4jContext = {
            "logger_context_1", "logger_context_2" }))
    interface Case01 {
        Order post(@OfHeader("AccountId") @OfLog4jContext final String accountId, Order order);

        @OfResponse(handler = "responseHandler1")
        void postWithVoidHandler(@OfHeader @OfLog4jContext final String accountId, Order order);

        @OfResponse(handler = "responseHandler1")
        void postWithVoidHandlerWithIntrospect(@OfHeader @OfLog4jContext final String accountId,
                @OfLog4jContext(introspect = true) Order order);
    }

    record Order(@OfLog4jContext("logger_context_2") String orderId, int amount) {
    }
}