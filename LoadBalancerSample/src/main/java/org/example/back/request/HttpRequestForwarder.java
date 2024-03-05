package org.example.back.request;

import java.net.http.HttpRequest;

public interface HttpRequestForwarder {
    HttpResponse send(HttpRequest httpRequest);
}
