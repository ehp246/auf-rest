package me.ehp246.test.embedded.restfn;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.rest.BodyDescriptor.JsonViewValue;
import me.ehp246.aufrest.api.rest.BodyDescriptor.ReturnValue;
import me.ehp246.aufrest.api.rest.BodyHandlerProvider;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;
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
    @Autowired
    private BodyHandlerProvider jsonBodyHandlerProvider;

    @Test
    void auth_01() {
        final var response = restFn.apply(new RestRequest() {

            @Override
            public String uri() {
                return "http://localhost:" + port + "/restfn/auth";
            }
        });

        Assertions.assertEquals(401, response.statusCode());
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
        }, new JsonViewValue(Logins.Login.class),
                () -> jsonBodyHandlerProvider.get(new ReturnValue(null, null, new Annotation[] { new OfBody() {

                    @Override
                    public Class<? extends Annotation> annotationType() {
                        return OfBody.class;
                    }

                    @Override
                    public Class<?>[] value() {
                        return new Class[] { Map.class };
                    }
                } })));

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
        }, new JsonViewValue(Logins.LoginName.class, new Annotation[] { new JsonView() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonView.class;
            }

            @Override
            public Class<?>[] value() {
                return new Class[] { RestPayload.class };
            }
        } }), () -> jsonBodyHandlerProvider.get(new ReturnValue(null, null, new Annotation[] { new OfBody() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return OfBody.class;
            }

            @Override
            public Class<?>[] value() {
                return new Class[] { Map.class };
            }
        } })));

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
        }, null, () -> jsonBodyHandlerProvider.get(new ReturnValue(null, null, new Annotation[] { new JsonView() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return JsonView.class;
            }

            @Override
            public Class<?>[] value() {
                return new Class[] { RestPayload.class };
            }
        }, new OfBody() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return OfBody.class;
            }

            @Override
            public Class<?>[] value() {
                return new Class[] { LoginName.class };
            }
        } })));

        final var body = (LoginName) response.body();

        Assertions.assertEquals(username, body.getUsername());
        Assertions.assertEquals(null, body.getPassword());
    }
}
