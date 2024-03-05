package org.example.back.request;

import lombok.Getter;

import java.util.Map;

@Getter
public class HttpResponse {
    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(int statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }
}
