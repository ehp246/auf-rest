package me.ehp246.aufrest.api.rest;

import java.net.http.HttpRequest.BodyPublisher;

public interface RequestPublisher {
    String contentType();

    BodyPublisher publisher();
}