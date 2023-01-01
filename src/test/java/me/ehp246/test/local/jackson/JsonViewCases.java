package me.ehp246.test.local.jackson;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.spi.RestPayload;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface JsonViewCases {
    @OfMapping("/login")
    Logins.Login post1(@JsonView(RestPayload.class) Logins.Login1 login);

    @OfMapping("/login")
    @JsonView(RestPayload.class)
    Logins.Login2 post2(Logins.Login login);

    @OfMapping("/login")
    Logins.Login post3(@JsonView(Logins.class) Logins.Login1 login);
}
