package me.ehp246.aufrest.core.byrest;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import me.ehp246.aufrest.api.rest.HttpUtils;
import me.ehp246.aufrest.api.rest.RestFn;
import me.ehp246.aufrest.api.rest.RestRequest;

/**
 * @author Lei Yang
 *
 */
class ByRestFactoryTest {
    private final AtomicReference<RestRequest> reqRef = new AtomicReference<>();

    private final RestFn client = request -> {
        reqRef.set(request);
        return Mockito.mock(HttpResponse.class);
    };

    private final MockEnvironment env = new MockEnvironment().withProperty("echo.base", "https://postman-echo.com")
            .withProperty("api.bearer.token", "ec3fb099-7fa3-477b-82ce-05547babad95")
            .withProperty("postman.username", "postman").withProperty("postman.password", "password");

    private final ByRestFactory factory = new ByRestFactory(cfg -> client, env::resolveRequiredPlaceholders);

    @BeforeEach
    void beforeEach() {
        reqRef.set(null);
    }

    @Test
    void defaults_001() {
        final var newInstance = factory.newInstance(GetCase001.class);

        Assertions.assertEquals(true, newInstance.hashCode() == newInstance.hashCode());
        Assertions.assertEquals(true, newInstance.equals(List.of(newInstance).get(0)));
        Assertions.assertEquals(true, Set.of(newInstance).contains(newInstance));
        Assertions.assertEquals(true, newInstance instanceof GetCase001);
        Assertions.assertEquals(true, !newInstance.toString().isBlank());
        Assertions.assertEquals(1, newInstance.getInc(0));
    }

    @Test
    void get001() {
        final var newInstance = factory.newInstance(GetCase001.class);

        newInstance.get();

        final var request = reqRef.get();

        Assertions.assertEquals("GET", request.method().toUpperCase());
    }

    @Test
    void get002() {
        final var newInstance = factory.newInstance(GetCase001.class);

        newInstance.get("");

        final var request = reqRef.get();

        Assertions.assertEquals("GET", request.method().toUpperCase());
    }

    @Test
    void get003() {
        final var newInstance = factory.newInstance(GetCase001.class);

        newInstance.get(0);

        Assertions.assertEquals("GET", reqRef.get().method().toUpperCase());
    }

    @Test
    void requestParam001() {
        final var newInstance = factory.newInstance(RequestParamCase001.class);

        newInstance.queryByParams("q1", "q2");

        final var request = reqRef.get();

        Assertions.assertEquals("https://postman-echo.com/get?query1=q1&query2=q2", request.uri());
    }

    @Test
    void requestParam002() {
        final var newInstance = factory.newInstance(RequestParamCase001.class);

        newInstance.queryEncoded("1 + 1 = 2");
        final var request = reqRef.get();

        Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2", request.uri(),
                "Should be encoded");
    }

    @Test
    void requestMap001() {
        final var newInstance = factory.newInstance(RequestParamCase001.class);

        newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"));
        final var request = reqRef.get();

        Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2&query2=q2", request.uri());
    }

    @Test
    void requestMap002() {
        final var newInstance = factory.newInstance(RequestParamCase001.class);

        newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"));
        final var request = reqRef.get();

        Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2&query2=q2", request.uri());
    }

    @Test
    void requestMap003() {
        final var newInstance = factory.newInstance(RequestParamCase001.class);

        newInstance.getByMap(Map.of("query 1", "1 + 1 = 2", "query2", "q2"), "q3");
        final var request = reqRef.get();

        Assertions.assertEquals("https://postman-echo.com/get?query+1=1+%2B+1+%3D+2&query2=q3", request.uri());
    }

    @Test
    void method001() {
        factory.newInstance(MethodTestCase001.class).get();

        Assertions.assertEquals("GET", reqRef.get().method());
    }

    @Test
    void method002() {
        Assertions.assertThrows(Exception.class, factory.newInstance(MethodTestCase001.class)::query);
    }

    @Test
    void method004() {
        factory.newInstance(MethodTestCase001.class).post();

        Assertions.assertEquals("POST", reqRef.get().method());
    }

    @Test
    void method005() {
        factory.newInstance(MethodTestCase001.class).delete();

        Assertions.assertEquals(RequestMethod.DELETE.name(), reqRef.get().method());
    }

    @Test
    void method006() {
        factory.newInstance(MethodTestCase001.class).put();

        Assertions.assertEquals(RequestMethod.PUT.name(), reqRef.get().method());
    }

    @Test
    void method007() {
        factory.newInstance(MethodTestCase001.class).patch();

        Assertions.assertEquals(RequestMethod.PATCH.name(), reqRef.get().method());
    }

    @Test
    void method008() {
        Assertions.assertThrows(Exception.class, factory.newInstance(MethodTestCase001.class)::query);
    }

    @Test
    void method009() {
        factory.newInstance(MethodTestCase001.class).create();

        Assertions.assertEquals("POST", reqRef.get().method());
    }

    @Test
    void method010() {
        factory.newInstance(MethodTestCase001.class).remove();

        Assertions.assertEquals("DELETE", reqRef.get().method());
    }

    @Test
    void method011() {
        factory.newInstance(MethodTestCase001.class).getBySomething();

        Assertions.assertEquals("GET", reqRef.get().method());
    }

    @Test
    void method012() {
        Assertions.assertThrows(RuntimeException.class, () -> factory.newInstance(MethodTestCase001.class).query(1));
    }

    @Test
    void method0013() {
        factory.newInstance(MethodTestCase001.class).postByName();

        Assertions.assertEquals("POST", reqRef.get().method());
    }

    @Test
    void header_001() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);
        newInstance.get("1234");

        Assertions.assertEquals("1234", reqRef.get().headers().get("x-correl-id").get(0), "should have the value");

        reqRef.set(null);

        newInstance.get("	");

        Assertions.assertEquals("	", reqRef.get().headers().get("x-correl-id").get(0));

        reqRef.set(null);

        newInstance.get((String) null);

        Assertions.assertEquals(0, reqRef.get().headers().size());
    }

    @Test
    void header_002() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.getBlank("1234");

        Assertions.assertEquals(1, reqRef.get().headers().size());
    }

    @Test
    void header_003() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        final var uuid = UUID.randomUUID();

        newInstance.get(uuid);

        Assertions.assertEquals(uuid.toString(), reqRef.get().headers().get("x-uuid").get(0),
                "should have call toString");
    }

    @Test
    void header_004() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.getRepeated("1", "2");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals(2, headers.get("x-correl-id").size(), "should concate");
    }

    @Test
    void header_005() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.getMultiple("1", "2");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size(), "should have both");
        Assertions.assertEquals("1", headers.get("x-span-id").get(0));
        Assertions.assertEquals("2", headers.get("x-trace-id").get(0));
    }

    @Test
    void header_006() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.get(List.of("CN", "EN", "   "));

        final var headers = reqRef.get().headers().get("accept-language");

        Assertions.assertEquals(3, headers.size());
        Assertions.assertEquals("CN", headers.get(0));
        Assertions.assertEquals("EN", headers.get(1));
        Assertions.assertEquals("   ", headers.get(2));
    }

    @Test
    void header_007() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.get(Map.of("CN", "EN", "   ", ""));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size(), "should have two headers");
        Assertions.assertEquals(1, headers.get("CN").size());
        Assertions.assertEquals(1, headers.get("   ").size());
    }

    @Test
    void header_008() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.get(Map.of("x-correl-id", "mapped", "accept-language", "CN"), "uuid");

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size(), "should have two headers");
        Assertions.assertEquals(2, headers.get("x-correl-id").size(), "should concate all values");
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void header_009() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.get(CollectionUtils
                .toMultiValueMap(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid"))));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size(), "should have two headers");
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_010() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.getMapOfList(Map.of("accept-language", List.of("CN", "EN"), "x-correl-id", List.of("uuid")));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size());
        Assertions.assertEquals(1, headers.get("x-correl-id").size());
        Assertions.assertEquals(2, headers.get("accept-language").size());
    }

    @Test
    void header_011() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.getListOfList(List.of(List.of("DE"), List.of("CN", "EN"), List.of("JP")));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(1, headers.size());
        Assertions.assertEquals(4, headers.get("accept-language").size());
    }

    @Test
    void header_012() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        final var nullList = new ArrayList<String>();
        nullList.add("EN");
        nullList.add(null);
        nullList.add("CN");

        newInstance.getListOfList(List.of(List.of("DE"), nullList, List.of("JP")));

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(1, headers.size(), "should filter out all nulls");
        Assertions.assertEquals(4, headers.get("accept-language").size());
    }

    @Test
    void header_013() {
        final var newInstance = factory.newInstance(RequestHeaderSpec001.class);

        newInstance.get(Map.of("x-correl-id", "mapped", "accept-language", "CN"), null);

        final var headers = reqRef.get().headers();

        Assertions.assertEquals(2, headers.size(), "should have two headers");
        Assertions.assertEquals(1, headers.get("x-correl-id").size(), "should filter out nulls");
        Assertions.assertEquals(1, headers.get("accept-language").size());
    }

    @Test
    void contentType_001() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get1();

        var req = reqRef.get();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals("i-accept", req.accept());
    }

    @Test
    void contentType_002() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get2();

        var req = reqRef.get();

        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_003() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case001.class);

        newInstance.get3();

        var req = reqRef.get();

        Assertions.assertEquals("m-type", req.contentType());
        Assertions.assertEquals("m-accept", req.accept());
    }

    @Test
    void contentType_004() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get1();

        var req = reqRef.get();

        Assertions.assertEquals("i-type", req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_005() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get2();

        var req = reqRef.get();

        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.contentType());
        Assertions.assertEquals(HttpUtils.APPLICATION_JSON, req.accept());
    }

    @Test
    void contentType_006() {
        final var newInstance = factory.newInstance(ContentTypeTestCases.Case002.class);

        newInstance.get3();

        var req = reqRef.get();

        Assertions.assertEquals("m-type", req.contentType());
        Assertions.assertEquals("m-accept", req.accept());
    }
}
