package me.ehp246.test.local.jackson;

import com.fasterxml.jackson.annotation.JsonView;

import me.ehp246.aufrest.api.spi.RestView;

/**
 * @author Lei Yang
 *
 */
record Login(@JsonView(RestView.class) String username, String password) {
}
