package me.ehp246.aufrest.provider.httpclient;

import static org.mockito.Mockito.CALLS_REAL_METHODS;

import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import me.ehp246.aufrest.api.rest.AuthProvider;
import me.ehp246.aufrest.api.rest.HeaderContext;
import me.ehp246.aufrest.api.rest.HeaderProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.core.rest.HttpRequestBuilder;
import me.ehp246.test.mock.MockContentPublisherProvider;
import me.ehp246.test.mock.MockReq;

/**
 * @author Lei Yang
 *
 */
class DefaultHttpRequestBuilderTest {
    private final static String POSTMAN_ECHO = "https://postman-echo.com";
    private final static String BEARER = "I'm a bearer.";
    private final static String BASIC = "I'm basic.";

    private final static MockContentPublisherProvider CONTENT_PROVIDER = new MockContentPublisherProvider();

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

    private final HttpRequestBuilder defBuilder = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, null);

    @BeforeEach
    void beforeEach() {
        HeaderContext.clear();
    }

    @Test
    void uri_01() {
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
    void uri_02() {
        final var url = POSTMAN_ECHO + "?foo1=bar1&foo2=bar2";

        final var httpReq = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return url;
            }

            @Override
            public String contentType() {
                return HttpUtils.APPLICATION_FORM_URLENCODED;
            }
        });

        // Allowing query on the path for now.
        Assertions.assertEquals(url, httpReq.uri().toString());
    }

    @Test
    void method_001() {
        Assertions.assertEquals("GET", defBuilder.apply(() -> "http://w.w.w").method());
    }

    @Test
    void auth_global_001() {
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, authProvider, null)
                .apply(() -> POSTMAN_ECHO + "/bearer");

        Assertions.assertEquals(BEARER, req.headers().firstValue(HttpUtils.AUTHORIZATION).get());
    }

    @Test
    void auth_global_002() {
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, authProvider, null)
                .apply(() -> POSTMAN_ECHO + "/basic");

        Assertions.assertEquals(BASIC, req.headers().firstValue(HttpUtils.AUTHORIZATION).get());
    }

    @Test
    void auth_global_003() {
        final DefaultHttpRequestBuilder builder = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, authProvider,
                null);

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
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, null)
                .apply(() -> POSTMAN_ECHO + "/basic");

        Assertions.assertEquals(0, req.headers().allValues(HttpUtils.AUTHORIZATION).size(), "Should have no Auth");
    }

    @Test
    void auth_global_005() {
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, null)
                .apply(() -> POSTMAN_ECHO + "/basic");

        Assertions.assertEquals(0, req.headers().allValues(HttpUtils.AUTHORIZATION).size(), "Should have no Auth");
    }

    @Test
    void auth_headerContext_01() {
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

        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, authProvider, null).apply(request);

        Assertions.assertEquals(0, req.headers().allValues("authorization").size(),
                "Request has Authorization explicitly off");
    }

    @Test
    void auth_headerContext_02() {
        final var value = UUID.randomUUID().toString();
        HeaderContext.add("authorization", value);

        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, null).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost";
            }

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("authorization", List.of(UUID.randomUUID().toString()));
            }
        });

        Assertions.assertEquals(value, req.headers().firstValue("authorization").get(), "Should come from the context");
    }

    @Test
    void auth_headerContext_03() {
        HeaderContext.add("authorization", UUID.randomUUID().toString());

        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, null).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost";
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

        Assertions.assertEquals("me2", req.headers().firstValue("authorization").get(), "Should come from the request");
    }

    @Test
    void auth_headerContext_04() {
        HeaderContext.add("authorization", UUID.randomUUID().toString());

        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, r -> null, null).apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost";
            }

            @Override
            public Supplier<String> authSupplier() {
                // Let the authProvider handle it.
                return null;
            }

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("authorization", List.of(UUID.randomUUID().toString()));
            }
        });

        Assertions.assertEquals(0, req.headers().allValues("authorization").size(),
                "Should come from the authProvider");
    }

    @Test
    void auth_headerProvider_01() {
        final var fromProvider = UUID.randomUUID().toString();
        // No authProvider.
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null,
                r -> Map.of(HttpUtils.AUTHORIZATION, List.of(fromProvider)), null, null).apply(new MockReq() {
                    // No request auth.
                    @Override
                    public Supplier<String> authSupplier() {
                        return null;
                    }

                    // Should be ignored
                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("authorization", List.of(UUID.randomUUID().toString()));
                    }
                });

        Assertions.assertEquals(1, req.headers().allValues("authorization").size(), "Should be from headerProvider");
        Assertions.assertEquals(fromProvider, req.headers().allValues("authorization").get(0),
                "Should be from headerProvider");
    }

    @Test
    void auth_headerProvider_02() {
        final var fromProvider = UUID.randomUUID().toString();
        HeaderContext.add("authorization", UUID.randomUUID().toString());

        // No authProvider. headerProvider should be ignored.
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null,
                r -> Map.of(HttpUtils.AUTHORIZATION, List.of(fromProvider)), null, null).apply(new MockReq() {
                    // No request auth.
                    @Override
                    public Supplier<String> authSupplier() {
                        return null;
                    }

                    // Should be ignored
                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("authorization", List.of(UUID.randomUUID().toString()));
                    }
                });

        Assertions.assertEquals(1, req.headers().allValues("authorization").size(), "Should come from headerProvider");
        Assertions.assertEquals(fromProvider, req.headers().allValues("authorization").get(0),
                "Should come from headerProvider");
    }

    @Test
    void timeout_global_reponse_001() {
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, null).apply(() -> "http://tonowhere");

        Assertions.assertEquals(true, req.timeout().isEmpty(), "Should have no timeout on request");
    }

    @Test
    void timeout_global_reponse_002() {
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, "PT24H")
                .apply(() -> "http://tonowhere");

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
        final var req = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, "PT2H").apply(new RestRequest() {

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
    void response_timeout_01() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DefaultHttpRequestBuilder(CONTENT_PROVIDER, null, null, null, "123"));
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

    DefaultHttpRequestBuilder builder(final HeaderProvider provider) {
        return new DefaultHttpRequestBuilder(new MockContentPublisherProvider(), null, provider, null, null);
    }

    @Test
    void headers_001() {
        final var name = UUID.randomUUID().toString();

        HeaderContext.set(name, UUID.randomUUID().toString());

        final var req = new MockReq() {

            @Override
            public Map<String, List<String>> headers() {
                return Map.of(name, List.of(super.reqId));
            }

        };

        final var headers = builder(r -> Map.of(name, List.of(UUID.randomUUID().toString()))).apply(req).headers().map()
                .get(name);

        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals(req.reqId, headers.get(0), "should be overwritten by Request");
    }

    @Test
    void headers_002() {
        final var name = UUID.randomUUID().toString();

        HeaderContext.set(name, UUID.randomUUID().toString());

        final var req = new MockReq() {

            @Override
            public Map<String, List<String>> headers() {
                return Map.of(reqId, List.of(super.reqId));
            }

        };

        final var headers = builder(r -> Map.of(name, List.of(UUID.randomUUID().toString()))).apply(req).headers()
                .map();

        Assertions.assertEquals(req.reqId, headers.get(req.reqId).get(0), "Request should be merged");
        Assertions.assertEquals(1, headers.get(name).size(), "Provider should overwrite Context");
        Assertions.assertEquals(HeaderContext.values(name).get(0), headers.get(name).get(0),
                "Provider should overwrite Context");
    }

    @Test
    void header_provider_001() {
        final var value = UUID.randomUUID().toString();

        final var headers = builder(r -> Map.of("header-provider", List.of(value))).apply(new MockReq()).headers()
                .map();

        Assertions.assertEquals(value, headers.get("header-provider").get(0));
    }

    @Test
    void header_provider_002() {
        final var name = UUID.randomUUID().toString();

        final var req = new MockReq() {

            @Override
            public Map<String, List<String>> headers() {
                return Map.of(name, List.of(super.reqId));
            }

        };

        final var headers = builder(r -> Map.of(name, List.of(UUID.randomUUID().toString()))).apply(req).headers().map()
                .get(name);

        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals(req.reqId, headers.get(0), "should be overwritten by Request");
    }

    @Test
    void header_provider_003() {
        final var name = UUID.randomUUID().toString();
        final var value = UUID.randomUUID().toString();

        HeaderContext.set(name, value);

        final var headers = builder(r -> Map.of(name, List.of(UUID.randomUUID().toString()))).apply(new MockReq())
                .headers().map().get(name);

        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals(value, headers.get(0), "should be overwritten by Context");
    }

    @Test
    void header_provider_004() {
        final var mockReq = new MockReq();
        final var reqRef = new AtomicReference<RestRequest>();

        builder(r -> {
            reqRef.set(r);
            return null;
        }).apply(mockReq);

        Assertions.assertEquals(true, reqRef.get() == mockReq, "Should be passed to the header provider");
    }

    @Test
    void header_priority_001() {
        final var name = "Filter-Them";

        HeaderContext.add(name.toLowerCase(), "2");

        final var headers = builder(req -> Map.of(name.toLowerCase(), List.of("3"))).apply(new MockReq() {

            @Override
            public Map<String, List<String>> headers() {
                return Map.of("filter-Them", List.of("1"));
            }

        }).headers();

        Assertions.assertEquals("1", headers.allValues(name).get(0));
    }

    @Test
    void header_priority_002() {
        final var name = "Filter-Them";

        HeaderContext.add(name, "2");

        final var headers = builder(req -> Map.of(name.toLowerCase(), List.of("3"))).apply(new MockReq()).headers();

        Assertions.assertEquals("2", headers.allValues(name).get(0));
    }

    @Test
    void header_priority_003() {
        final var name = "Filter-These";

        HeaderContext.add(name, "2");

        final var headers = builder(req -> Map.of("merge-these", List.of("1", "2"))).apply(new MockReq()).headers();

        Assertions.assertEquals("2", headers.allValues(name).get(0));
        Assertions.assertEquals(2, headers.allValues("merge-these").size());
    }

    @Test
    void body_01() {
        final var expected = BodyPublishers.noBody();

        final var req = defBuilder.apply(new RestRequest() {
            @Override
            public String uri() {
                return "http://nowhere";
            }

            @Override
            public Object body() {
                return expected;
            }

        });

        Assertions.assertEquals(expected, req.bodyPublisher().get());
    }

    @Test
    void body_03() {
        final var req = defBuilder.apply(new RestRequest() {
            @Override
            public String uri() {
                return "http://nowhere";
            }
        });

        Assertions.assertEquals(0, req.bodyPublisher().get().contentLength());
    }

    @Test
    void conneg_001() {
        final var req = defBuilder.apply(() -> "http://nowhere");

        final var contentType = req.headers().map().get("content-type");

        Assertions.assertEquals(1, contentType.size());
        Assertions.assertEquals("", contentType.get(0), "should be passed through");

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

        Assertions.assertEquals(0, headers.allValues(HttpUtils.AUTHORIZATION).size(), "Should filter blank strings");
    }

    @Test
    void request_builder_001() {
        final Supplier<HttpRequest.Builder> reqBuilderSupplier = Mockito.mock(MockRequestBuilderSupplier.class,
                CALLS_REAL_METHODS);

        final var builder = new DefaultHttpRequestBuilder(CONTENT_PROVIDER, reqBuilderSupplier, null, null, null);

        final int count = (int) (Math.random() * 20);

        IntStream.range(0, count).forEach(i -> builder.apply(() -> POSTMAN_ECHO));

        Mockito.verify(reqBuilderSupplier,
                Mockito.times(count).description("Should ask for a new builder for each request")).get();
    }

    @Test
    void query_01() {
        final var httpReq = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return POSTMAN_ECHO;
            }

            @Override
            public Map<String, List<String>> queries() {
                return null;
            }
        });

        Assertions.assertEquals(true, httpReq.uri().getQuery() == null);
    }

    @Test
    void query_02() {
        final var httpReq = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return POSTMAN_ECHO;
            }

            @Override
            public Map<String, List<String>> queries() {
                return Map.of();
            }
        });

        Assertions.assertEquals(true, httpReq.uri().getQuery() == null);
    }

    @Test
    void query_03() {
        final var httpReq = defBuilder.apply(new RestRequest() {

            @Override
            public String uri() {
                return POSTMAN_ECHO;
            }

            @Override
            public Map<String, List<String>> queries() {
                return Map.of("1", List.of("1"));
            }
        });

        Assertions.assertEquals(true, httpReq.uri().getQuery() != null);
    }
}
