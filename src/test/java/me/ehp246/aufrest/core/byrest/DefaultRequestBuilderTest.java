package me.ehp246.aufrest.core.byrest;

import static org.mockito.Mockito.CALLS_REAL_METHODS;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RequestBuilder;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.mock.MockReq;
import me.ehp246.aufrest.provider.httpclient.DefaultRequestBuilder;
import me.ehp246.aufrest.provider.httpclient.MockRequestBuilderSupplier;

/**
 * @author Lei Yang
 *
 */
class DefaultRequestBuilderTest {
    private final static String POSTMAN_ECHO = "https://postman-echo.com";
    private final static String BEARER = "I'm a bearer.";
    private final static String BASIC = "I'm basic.";

    private final AuthProvider authProvider = new AuthProvider() {
        private int count = 0;

        @Override
        public String get(final RestRequest req) {
            final var uri = req.uri();
            if (uri.toString().contains("bearer")) {
                return BEARER;
            } else if (uri.toString().contains("basic")) {
                return BASIC;
            } else if (uri.toString().contains("count")) {
                return ++count + "";
            }
            return null;
        }
    };

    private final RequestBuilder defBuilder = new DefaultRequestBuilder(null, null, null, null, null);

    @BeforeEach
    void beforeEach() {
        HeaderContext.clear();
    }

    @Test
    void id_001() {
        final var id = UUID.randomUUID().toString();
        Assertions.assertEquals(id, defBuilder.apply(new RestRequest() {

            @Override
            public String id() {
                return id;
            }

            @Override
            public String uri() {
                return "http://w.w.w";
            }
        }).headers().firstValue(HttpUtils.REQUEST_ID).get());
    }

    @Test
    void uri_001() {
        final var url = POSTMAN_ECHO + "get?foo1=bar1&foo2=bar2";

        final var httpReq = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return url;
            }

            @Override
            public String method() {
                return "POST";
            }
        });

        Assertions.assertEquals("POST", httpReq.method());
        Assertions.assertEquals(url, httpReq.uri().toString());
    }

    @Test
    void method_001() {
        Assertions.assertEquals("GET", defBuilder.apply(() -> "http://w.w.w").method());
    }

    @Test
    void auth_global_001() {
        final var req = new DefaultRequestBuilder(null, null, authProvider, null, null)
                .apply(() -> POSTMAN_ECHO + "/bearer");

        Assertions.assertEquals(BEARER, req.headers().firstValue(HttpUtils.AUTHORIZATION).get());
    }

    @Test
    void auth_global_002() {
        final var req = new DefaultRequestBuilder(null, null, authProvider, null, null)
                .apply(() -> POSTMAN_ECHO + "/basic");

        Assertions.assertEquals(BASIC, req.headers().firstValue(HttpUtils.AUTHORIZATION).get());
    }

    @Test
    void auth_global_003() {
        final DefaultRequestBuilder builder = new DefaultRequestBuilder(null, null, authProvider, null, null);

        final var request = (RestRequest) () -> POSTMAN_ECHO + "/count";

        var req = builder.apply(request);

        Assertions.assertEquals(1, req.headers().allValues("authorization").size());

        Assertions.assertEquals("1", req.headers().firstValue(HttpUtils.AUTHORIZATION).get());

        req = builder.apply(request);

        Assertions.assertEquals("2", req.headers().firstValue(HttpUtils.AUTHORIZATION).get(),
                "Should be dynamic on invocations");
    }

    @Test
    void auth_global_004() {
        final var req = new DefaultRequestBuilder(null, null, null, null, null).apply(() -> POSTMAN_ECHO + "/basic");

        Assertions.assertEquals(0, req.headers().allValues(HttpUtils.AUTHORIZATION).size(), "Should have no Auth");
    }

    @Test
    void auth_global_005() {
        final var req = new DefaultRequestBuilder(null, null, null, null, null).apply(() -> POSTMAN_ECHO + "/basic");

        Assertions.assertEquals(0, req.headers().allValues(HttpUtils.AUTHORIZATION).size(), "Should have no Auth");
    }

    @Test
    void auth_header_001() {
        HeaderContext.add("authorization", UUID.randomUUID().toString());

        final var request = new MockReq() {
            @Override
            public Map<String, List<String>> headers() {
                return Map.of("authorization", List.of(UUID.randomUUID().toString()));
            }

            @Override
            public Supplier<String> authSupplier() {
                // Turning off Auth explicitly overwriting AuthProvider
                return () -> null;
            }

        };

        final var req = new DefaultRequestBuilder(null, null, authProvider, null, null).apply(request);

        Assertions.assertEquals(0, req.headers().allValues("authorization").size(),
                "Request has Authorization explicitly off");
    }

    @Test
    void auth_header_002() {
        HeaderContext.add("authorization", UUID.randomUUID().toString());

        final var req = new DefaultRequestBuilder(null, null, null, null, null).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://w.w.w";
            }

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("authorization", List.of(UUID.randomUUID().toString()));
            }
        });

        Assertions.assertEquals(true, req.headers().firstValue("authorization").isEmpty(),
                "Should not come from usual headers");
    }

    @Test
    void auth_header_003() {
        HeaderContext.add("authorization", UUID.randomUUID().toString());

        final var req = new DefaultRequestBuilder(null, null, null, null, null).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://w.w.w";
            }

            @Override
            public Supplier<String> authSupplier() {
                return () -> "me2";
            }

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("authorization", List.of(UUID.randomUUID().toString()));
            }
        });

        Assertions.assertEquals("me2", req.headers().firstValue("authorization").get(),
                "Should not come from usual headers");
    }

    @Test
    void timeout_global_reponse_001() {
        final var req = new DefaultRequestBuilder(null, null, null, null, null).apply(() -> "http://tonowhere");

        Assertions.assertEquals(true, req.timeout().isEmpty(), "Should have no timeout on request");
    }

    @Test
    void timeout_global_reponse_002() {
        final var req = new DefaultRequestBuilder(null, null, null, null, "PT24H").apply(() -> "http://tonowhere");

        Assertions.assertEquals(1, req.timeout().get().toDays(), "Should have global timeout");
    }

    @Test
    void timeout_per_request_001() {
        final var req = defBuilder.apply(new RestRequest() {

            @Override
            public Duration timeout() {
                return Duration.ofHours(1);
            }

            @Override
            public String uri() {
                return "http://tonowhere";
            }
        });

        Assertions.assertEquals(60, req.timeout().get().toMinutes(), "Should have take timeout on the request");
    }

    @Test
    void timeout_per_request_002() {
        final var req = new DefaultRequestBuilder(null, null, null, null, "PT2H").apply(new RestRequest() {

            @Override
            public Duration timeout() {
                return Duration.ofHours(10);
            }

            @Override
            public String uri() {
                return "http://tonowhere";
            }
        });
        Assertions.assertEquals(600, req.timeout().get().toMinutes(),
                "Should have take timeout on the request instead of Global");
    }

    @Test
    void request_header_001() {
        final var req = defBuilder.apply(new MockReq() {

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("accept-language", List.of("CN", "EN", ""), "x-correl-id", List.of("uuid"));
            }

        });

        final var map = req.headers().map();

        Assertions.assertEquals(2, map.get("accept-language").size(), "should filter out all blank values");
        Assertions.assertEquals(1, map.get("x-correl-id").size());
    }

    @Test
    void header_context_001() {
        HeaderContext.set("accept-language", "DE");

        final var req = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://nowhere";
            }

            @Override
            public Map<String, List<String>> headers() {
                return null;
            }

        });

        final var map = req.headers().map();

        final var accept = map.get("accept-language");

        Assertions.assertEquals(1, accept.size(), "should have context headers");
        Assertions.assertEquals("DE", accept.get(0));
    }

    @Test
    void header_context_002() {
        HeaderContext.set("accept-language", "DE");

        final var req = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://nowhere";
            }

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("x-correl-id", List.of("uuid"));
            }

        });

        final var map = req.headers().map();

        final var accept = map.get("accept-language");

        Assertions.assertEquals(1, accept.size(), "should have context headers");
        Assertions.assertEquals("DE", accept.get(0));
        Assertions.assertEquals(1, map.get("x-correl-id").size(), "should merge");
    }

    @Test
    void header_context_003() {
        HeaderContext.set("accept-language", "DE");

        final var req = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://nowhere";
            }

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("accept-language", List.of("EN"));
            }

        });

        final var map = req.headers().map();

        final var accept = map.get("accept-language");

        Assertions.assertEquals(1, accept.size(), "should override context headers");
        Assertions.assertEquals("EN", accept.get(0));
    }

    @Test
    void conneg_001() {
        final var req = defBuilder.apply(() -> "http://nowhere");

        final var contentType = req.headers().map().get("content-type");

        Assertions.assertEquals(1, contentType.size());
        Assertions.assertEquals("application/json", contentType.get(0));

        final var accept = req.headers().map().get("accept");

        Assertions.assertEquals(1, accept.size());
        Assertions.assertEquals("application/json", accept.get(0));
    }

    @Test
    void conneg_002() {
        final var req = defBuilder.apply(new RestRequest() {
            @Override
            public String uri() {
                return "http://nowhere";
            }

            @Override
            public String contentType() {
                return "produce this";
            }

            @Override
            public String accept() {
                return "consume that";
            }

        });

        final var contentType = req.headers().map().get("content-type");

        Assertions.assertEquals(1, contentType.size());
        Assertions.assertEquals("produce this", contentType.get(0));

        final var accept = req.headers().map().get("accept");

        Assertions.assertEquals(1, accept.size());
        Assertions.assertEquals("consume that", accept.get(0));
    }

    @Test
    void auth_null_001() {
        final var headers = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return POSTMAN_ECHO;
            }

            @Override
            public String method() {
                return "POST";
            }
        }).headers();

        Assertions.assertEquals(true, headers.firstValue(HttpUtils.AUTHORIZATION).isEmpty());
    }

    @Test
    void auth_req_002() {
        final var headers = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return POSTMAN_ECHO;
            }

            @Override
            public String method() {
                return "POST";
            }

            @Override
            public Supplier<String> authSupplier() {
                return () -> "req auth header";
            }

        }).headers();

        Assertions.assertEquals("req auth header", headers.firstValue(HttpUtils.AUTHORIZATION).get(),
                "Should be from request");
    }

    @Test
    void auth_req_004() {
        final var headers = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return POSTMAN_ECHO + "/bearer";
            }

            @Override
            public String method() {
                return "POST";
            }

            @Override
            public Supplier<String> authSupplier() {
                return () -> null;
            }
        }).headers();

        Assertions.assertEquals(true, headers.firstValue(HttpUtils.AUTHORIZATION).isEmpty(), "should be from request");
    }

    @Test
    void auth_req_005() {
        final var headers = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return POSTMAN_ECHO + "/bearer";
            }

            @Override
            public String method() {
                return "POST";
            }

            @Override
            public Supplier<String> authSupplier() {
                return () -> "   ";
            }
        }).headers();

        Assertions.assertEquals(true, headers.firstValue(HttpUtils.AUTHORIZATION).isEmpty());
    }

    @Test
    void requst_builder_001() {
        final Supplier<HttpRequest.Builder> reqBuilderSupplier = Mockito.mock(MockRequestBuilderSupplier.class,
                CALLS_REAL_METHODS);

        final var builder = new DefaultRequestBuilder(reqBuilderSupplier, null, null, null, null);

        final int count = (int) (Math.random() * 20);

        IntStream.range(0, count).forEach(i -> builder.apply(() -> POSTMAN_ECHO));

        Mockito.verify(reqBuilderSupplier,
                Mockito.times(count).description("Should ask for a new builder for each request")).get();
    }
}
