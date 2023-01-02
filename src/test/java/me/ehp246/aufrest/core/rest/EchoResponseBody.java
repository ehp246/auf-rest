package me.ehp246.aufrest.core.rest;

import java.util.Map;

interface EchoResponseBody {
    Map<String, String> getArgs();

    Map<String, String> getHeaders();

    String getUrl();

    String getData();

    String getJson();
}