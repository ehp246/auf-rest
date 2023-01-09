package me.ehp246.test.embedded.jackson;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfBody;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface JsonViewCases {
    @OfMapping("/login")
    Logins.Login post1(@OfBody(view = RestView.class) Logins.Login1 login);

    @OfMapping("/login")
    @OfBody(view = RestView.class)
    Logins.Login2 post2(Logins.Login login);

    @OfMapping("/login")
    Logins.Login post3(@OfBody(view = Logins.class) Logins.Login1 login);
}
