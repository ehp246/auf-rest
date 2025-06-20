package me.ehp246.test.embedded.restfn;

import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.ehp246.aufrest.api.exception.BadGatewayException;
import me.ehp246.aufrest.api.exception.BadRequestException;
import me.ehp246.aufrest.api.exception.ClientErrorException;
import me.ehp246.aufrest.api.exception.ForbiddenException;
import me.ehp246.aufrest.api.exception.GatewayTimeoutException;
import me.ehp246.aufrest.api.exception.InternalServerErrorException;
import me.ehp246.aufrest.api.exception.NotAcceptableException;
import me.ehp246.aufrest.api.exception.NotAllowedException;
import me.ehp246.aufrest.api.exception.NotAuthorizedException;
import me.ehp246.aufrest.api.exception.NotFoundException;
import me.ehp246.aufrest.api.exception.NotSupportedException;
import me.ehp246.aufrest.api.exception.RedirectionException;
import me.ehp246.aufrest.api.exception.ServerErrorException;
import me.ehp246.aufrest.api.exception.ServiceUnavailableException;
import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.BodyHandlerType;
import me.ehp246.aufrest.api.rest.BodyHandlerType.Inferring;
import me.ehp246.aufrest.api.rest.ContentPublisherProvider;
import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.InferringBodyHandlerProvider;
import me.ehp246.aufrest.api.rest.JacksonTypeDescriptor;
import me.ehp246.aufrest.api.rest.ParameterizedTypeBuilder;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.spi.RestView;
import me.ehp246.test.embedded.restfn.Logins.Login;
import me.ehp246.test.embedded.restfn.Logins.LoginName;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
class RestFnTest {
    @Value("${local.server.port}")
    private int port;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestFn restFn;
    @Autowired
    private InferringBodyHandlerProvider handlerProvider;
    @Autowired
    private ContentPublisherProvider publisherProvider;

    private RestRequest new401Req() {
        return new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }

            @Override
            public Supplier<String> authSupplier() {
                return ""::toString;
            }
        };
    }

    private RestRequest newLoginsReq(final Login login) {
        return new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/logins";
            }

            @Override
            public Object body() {
                return login;
            }
        };
    }

    private RestRequest newLoginReq(final Login login) {
        return new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }
        };
    }

    @Test
    void auth_01() {
        final var threw = Assertions.assertThrows(UnhandledResponseException.class,
                () -> this.restFn.applyForResponse(new401Req()), "should use the provided header");

        Assertions.assertEquals(401, threw.statusCode());
    }

    @Test
    void auth_02() {
        final var response = restFn.applyForResponse(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }
        });

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void auth_03() {
        Assertions.assertEquals(200,
                restFn.applyForResponse(() -> "http://localhost:" + port + "/restfn/auth").statusCode(),
                "should authorize from the global bean");
    }

    @Test
    void auth_04() {
        final var threw = Assertions.assertThrows(UnhandledResponseException.class,
                () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/auth";
                    }

                    @Override
                    public Supplier<String> authSupplier() {
                        return () -> null;
                    }
                }), "should suppress the global provider");

        Assertions.assertEquals(401, threw.statusCode());
    }

    @Test
    void uri_01() {
        restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/{root}/{path}";
            }

            @Override
            public Map<String, ?> paths() {
                return Map.of("root", "restfn", "path", "auth");
            }
        });
    }

    @Test
    void body_01() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();

        final var login = new Logins.Login(username, password);
        final var body = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }
        });

        Assertions.assertEquals(username, body.get("username"));
        Assertions.assertEquals(password, body.get("password"));
    }

    @Test
    void requestBody_view_01() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();

        final var login = new Logins.LoginName() {

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };

        final var descriptor = JacksonTypeDescriptor.of(Logins.LoginName.class, RestView.class);
        final var response = restFn.applyForResponse(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }

            @Override
            public JacksonTypeDescriptor bodyDescriptor() {
                return descriptor;
            }

        });

        final var body = response.body();

        Assertions.assertEquals(username, body.get("username"));
        Assertions.assertEquals(null, body.get("password"), "should not have it");
    }

    @Test
    void requestBody_view_02() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();

        final var descriptor = JacksonTypeDescriptor.of(Logins.LoginName.class, RestView.class);
        final var login = new Logins.LoginName() {

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };
        final var body = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }

            @Override
            public JacksonTypeDescriptor bodyDescriptor() {
                return descriptor;
            }

        });

        Assertions.assertEquals(username, body.get("username"));
        Assertions.assertEquals(null, body.get("password"));
    }

    @Test
    void contentPublisher_01() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();
        final var login = new Logins.LoginName() {

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };

        final var contentPublisher = this.publisherProvider.get(login,
                JacksonTypeDescriptor.of(Logins.LoginName.class, null));

        final var response = restFn.applyForResponse(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public String contentType() {
                return contentPublisher.contentType();
            }

            @Override
            public Object body() {
                return contentPublisher.publisher();
            }
        });

        final var body = response.body();

        Assertions.assertEquals(username, body.get("username"));
        Assertions.assertEquals(password, body.get("password"), "should bypass the built-in publisher");
    }

    @Test
    void requestBody_providedPublisher_01() throws JsonProcessingException {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();
        final var login = new Logins.LoginName() {

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public String getPassword() {
                return password;
            }
        };

        final var bodyPublisher = BodyPublishers.ofString(objectMapper.writeValueAsString(login));
        final var response = restFn.applyForResponse(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public String contentType() {
                return HttpUtils.APPLICATION_JSON;
            }

            @Override
            public Object body() {
                return bodyPublisher;
            }
        });

        final var body = response.body();

        Assertions.assertEquals(username, body.get("username"));
        Assertions.assertEquals(password, body.get("password"), "should bypass the built-in publisher");
    }

    @Test
    void body_responseView_01() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();

        final var login = new Logins.Login(username, password);
        final var body = (LoginName) restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }
        }, new BodyHandlerType.Inferring<LoginName>(LoginName.class, RestView.class));

        Assertions.assertEquals(username, body.getUsername());
        Assertions.assertEquals(null, body.getPassword());
    }

    @SuppressWarnings("unchecked")
    @Test
    void responseBody_reifying_01() {
        final var login = new Logins.Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var body = (List<LoginName>) restFn.apply(newLoginsReq(login),
                new Inferring<>(ParameterizedTypeBuilder.ofList(LoginName.class), RestView.class));

        Assertions.assertEquals(1, body.size());
        Assertions.assertEquals(ArrayList.class, body.getClass());
        Assertions.assertEquals(login.username(), body.get(0).getUsername());
        Assertions.assertEquals(null, body.get(0).getPassword());
    }

    @Test
    void body_simple_01() {
        final var login = new Logins.Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var body = restFn.apply(newLoginReq(login), Logins.Login.class);

        Assertions.assertEquals(login.username(), body.username());
        Assertions.assertEquals(login.password(), body.password());
    }

    @Test
    void header_01() {
        final var login = new Logins.Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        final var headers = restFn.applyForHeaders(newLoginsReq(login));

        Assertions.assertEquals(true, headers.map().size() >= 10);
    }

    @Test
    void responseBody_providedHandler_01() {
        final var login = new Logins.Login(UUID.randomUUID().toString(), UUID.randomUUID().toString());

        /*
         * Get a handler that doesn't support View.
         */
        final var handler = this.handlerProvider.get(new BodyHandlerType.Inferring<LoginName>(LoginName.class));

        final var respons = (LoginName) restFn
                .applyForResponse(newLoginReq(login), new BodyHandlerType.Provided<>(handler)).body();

        /*
         * The provided handler doesn't support view. All properties should be /*
         * populated.
         */
        Assertions.assertEquals(login.username(), respons.getUsername());
        Assertions.assertEquals(login.password(), respons.getPassword());

        /*
         * Get a handler that does support View.
         */
        final var handlerWithView = this.handlerProvider
                .get(new BodyHandlerType.Inferring<LoginName>(LoginName.class, RestView.class));

        /**
         * Use it on the response.
         */
        final var responseWithView = (LoginName) restFn
                .applyForResponse(newLoginReq(login), new BodyHandlerType.Provided<>(handlerWithView)).body();

        Assertions.assertEquals(login.username(), responseWithView.getUsername());
        Assertions.assertEquals(null, responseWithView.getPassword());
    }

    @Test
    void errorType_01() {
        final var expected = UUID.randomUUID().toString();
        final var response = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/error";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("10"));
                    }

                    @Override
                    public Map<String, List<String>> queries() {
                        return Map.of("message", List.of(expected));
                    }

                }, new BodyHandlerType.Inferring<String>(String.class, null, Error.class))).getCause().httpResponse();

        Assertions.assertEquals(410, response.statusCode());

        final var error = (Error) response.body();

        Assertions.assertEquals(10, error.code());
        Assertions.assertEquals(expected, error.message());
    }

    @Test
    void status_410() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("410"));
                    }

                })).getCause();

        Assertions.assertEquals(ClientErrorException.class, cause.getClass());
        Assertions.assertEquals(410, cause.statusCode());
    }

    @Test
    void status_500() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("500"));
                    }

                })).getCause();

        Assertions.assertEquals(InternalServerErrorException.class, cause.getClass());
        Assertions.assertEquals(500, cause.statusCode());
    }

    @Test
    void status_502() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("502"));
                    }

                })).getCause();

        Assertions.assertEquals(BadGatewayException.class, cause.getClass());
        Assertions.assertEquals(502, cause.statusCode());
    }

    @Test
    void status_503() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("503"));
                    }

                })).getCause();

        Assertions.assertEquals(ServiceUnavailableException.class, cause.getClass());
        Assertions.assertEquals(503, cause.statusCode());
    }

    @Test
    void status_504() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("504"));
                    }

                })).getCause();

        Assertions.assertEquals(GatewayTimeoutException.class, cause.getClass());
        Assertions.assertEquals(504, cause.statusCode());
    }

    @Test
    void status_510() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("510"));
                    }

                })).getCause();

        Assertions.assertEquals(ServerErrorException.class, cause.getClass());
        Assertions.assertEquals(510, cause.statusCode());
    }

    @Test
    void status_400() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("400"));
                    }

                })).getCause();

        Assertions.assertEquals(BadRequestException.class, cause.getClass());
        Assertions.assertEquals(400, cause.statusCode());
    }

    @Test
    void status_401() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("401"));
                    }

                })).getCause();

        Assertions.assertEquals(NotAuthorizedException.class, cause.getClass());
    }

    @Test
    void status_403() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("403"));
                    }

                })).getCause();

        Assertions.assertEquals(ForbiddenException.class, cause.getClass());
    }

    @Test
    void status_404() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("404"));
                    }

                })).getCause();

        Assertions.assertEquals(NotFoundException.class, cause.getClass());
    }

    @Test
    void status_405() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("405"));
                    }

                })).getCause();

        Assertions.assertEquals(NotAllowedException.class, cause.getClass());
    }

    @Test
    void status_406() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("406"));
                    }

                })).getCause();

        Assertions.assertEquals(NotAcceptableException.class, cause.getClass());
    }

    @Test
    void status_415() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("415"));
                    }

                })).getCause();

        Assertions.assertEquals(NotSupportedException.class, cause.getClass());
    }

    @Test
    void status_300() {
        final var cause = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.applyForResponse(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/status";
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("300"));
                    }

                })).getCause();

        Assertions.assertEquals(RedirectionException.class, cause.getClass());
        Assertions.assertEquals(300, cause.statusCode());
    }

    @Test
    void status_3xx() {
        final var ref = new int[1];
        for (int i = 300; i < 400; i++) {
            ref[0] = i;
            final var cause = Assertions.assertThrows(UnhandledResponseException.class,
                    () -> this.restFn.applyForResponse(new RestRequest() {

                        @Override
                        public String uri() {
                            return "http://localhost:" + port + "/restfn/status";
                        }

                        @Override
                        public Map<String, List<String>> headers() {
                            return Map.of("code", List.of(ref[0] + ""));
                        }

                    })).getCause();

            Assertions.assertEquals(RedirectionException.class, cause.getClass());
            Assertions.assertEquals(i, cause.statusCode());
        }
    }
}
