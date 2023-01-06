package me.ehp246.test.embedded.restfn;

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

import me.ehp246.aufrest.api.exception.UnhandledResponseException;
import me.ehp246.aufrest.api.rest.RestBodyDescriptor;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
import me.ehp246.aufrest.api.rest.RestResponseDescriptor;
import me.ehp246.aufrest.api.spi.RestPayload;
import me.ehp246.test.embedded.restfn.Logins.LoginName;

/**
 * @author Lei Yang
 *
 */
@SpringBootTest(classes = { AppConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
        "me.ehp246.aufrest.restlogger.enabled=true" })
class RestFnTest {
    @Value("${local.server.port}")
    private int port;
    @Autowired
    private RestFn restFn;

    private RestRequest new401Req() {
        return new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }
        };
    }

    @SuppressWarnings("unchecked")
    @Test
    void auth_01() {
        final var threw = Assertions.assertThrows(UnhandledResponseException.class,
                () -> this.restFn.apply(new401Req()));
        threw.printStackTrace();

        Assertions.assertEquals(401, threw.statusCode());
        Assertions.assertEquals(true, threw.getCause().body() instanceof Map);
        Assertions.assertEquals(4, ((Map<String, Object>) threw.getCause().body()).entrySet().size());
    }

    @Test
    void auth_02() {
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }

            @Override
            public Supplier<String> authSupplier() {
                return "Basic YmFzaWN1c2VyOnBhc3N3b3Jk"::toString;
            }

        });

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void body_01() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();

        final var login = new Logins.Login(username, password);
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }

            @Override
            public Supplier<String> authSupplier() {
                return "Basic YmFzaWN1c2VyOnBhc3N3b3Jk"::toString;
            }
        });

        @SuppressWarnings("unchecked")
        final var body = (Map<String, Object>) response.body();

        Assertions.assertEquals(username, body.get("username"));
        Assertions.assertEquals(password, body.get("password"));
    }

    @Test
    void body_requestView_01() {
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
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }

            @Override
            public Supplier<String> authSupplier() {
                return "Basic YmFzaWN1c2VyOnBhc3N3b3Jk"::toString;
            }
        }, new RestBodyDescriptor<Logins.LoginName>() {

            @Override
            public Class<Logins.LoginName> type() {
                return Logins.LoginName.class;
            }

            @Override
            public Class<?> view() {
                return RestPayload.class;
            }
        });

        @SuppressWarnings("unchecked")
        final var body = (Map<String, Object>) response.body();

        Assertions.assertEquals(username, body.get("username"));
        Assertions.assertEquals(null, body.get("password"));
    }

    @Test
    void body_responseView_01() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();

        final var login = new Logins.Login(username, password);
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/login";
            }

            @Override
            public Object body() {
                return login;
            }

            @Override
            public Supplier<String> authSupplier() {
                return "Basic YmFzaWN1c2VyOnBhc3N3b3Jk"::toString;
            }
        }, (RestResponseDescriptor<LoginName>) new RestResponseDescriptor.InferringDescriptor<LoginName>() {

            @Override
            public Class<LoginName> type() {
                return LoginName.class;
            }

            @Override
            public Class<?> view() {
                return RestPayload.class;
            }
        });

        final var body = response.body();

        Assertions.assertEquals(username, body.getUsername());
        Assertions.assertEquals(null, body.getPassword());
    }

    @Test
    void errorType_01() {
        final var expected = UUID.randomUUID().toString();
        final var response = Assertions
                .assertThrows(UnhandledResponseException.class, () -> this.restFn.apply(new RestRequest() {

                    @Override
                    public String uri() {
                        return "http://localhost:" + port + "/restfn/error";
                    }

                    @Override
                    public Supplier<String> authSupplier() {
                        return "Basic YmFzaWN1c2VyOnBhc3N3b3Jk"::toString;
                    }

                    @Override
                    public Map<String, List<String>> headers() {
                        return Map.of("code", List.of("10"));
                    }

                    @Override
                    public Map<String, List<String>> queries() {
                        return Map.of("message", List.of(expected));
                    }

                }, (RestResponseDescriptor<String>) new RestResponseDescriptor.InferringDescriptor<String>() {

                    @Override
                    public Class<String> type() {
                        return String.class;
                    }

                    @Override
                    public Class<?> errorType() {
                        return Error.class;
                    }
                })).getCause().httpResponse();

        Assertions.assertEquals(410, response.statusCode());

        final var error = (Error) response.body();

        Assertions.assertEquals(10, error.code());
        Assertions.assertEquals(expected, error.message());
    }
}
