package me.ehp246.aufrest.integration.local.method.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lei Yang
 *
 */
@RestController
@RequestMapping(value = "/method", produces = MediaType.TEXT_PLAIN_VALUE)
class MethodController {
    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.PATCH,
            RequestMethod.DELETE }, produces = MediaType.TEXT_PLAIN_VALUE)
    public String getMethod(final HttpServletRequest request) {
        return request.getMethod();
    }
}
