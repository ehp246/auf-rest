package me.ehp246.test.local.jackson;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.OfMapping;
import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
@ByRest(value = "http://localhost:${local.server.port}/body")
interface JacksonCases {
    @OfMapping("/login")
    Login post(@JsonView(RestView.class) Login login);
}
