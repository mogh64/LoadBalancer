package org.example.front.request;

import com.sun.net.httpserver.HttpExchange;
import org.example.back.Server;

import java.net.URI;
import java.net.http.HttpRequest;

public class HttpRequestBuilder {
    public HttpRequest createHttpRequest(HttpExchange clientRequest, Server destinationServer) {
        URI destinationUri = createDestinationUri(clientRequest,destinationServer);
        var httpRequestBuilder = HttpRequest.newBuilder(destinationUri);
        switch (clientRequest.getRequestMethod().toLowerCase()) {
            case "get":
                httpRequestBuilder = httpRequestBuilder.GET();
                break;
            case "post":
                break;
        }

        return httpRequestBuilder.build();
    }

    private URI createDestinationUri(HttpExchange clientRequest, Server server) {
        return URI.create(server.getAddress() + clientRequest.getRequestURI().getPath());
    }
}
