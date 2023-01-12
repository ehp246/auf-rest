package me.ehp246.test.embedded.view;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfRequest;
import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface ViewCases {
    @OfRequest("/login")
    Logins.Login postRequestAllBlank(@JsonView(String.class) Logins.RequestWithView login);

    @OfRequest("/login")
    Logins.Login postRequestWithPassword(@JsonView(Logins.class) Logins.RequestWithView login);

    @OfRequest("/login")
    Logins.ResponseWithView postResponseWithDefault(Logins.Login login);

    @OfRequest("/login")
    @JsonView(RestView.class)
    Logins.ResponseWithView postResponseWithPassword(Logins.Login login);
}
