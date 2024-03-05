package org.example.front.request;


import com.sun.net.httpserver.HttpExchange;
import org.example.front.exception.InvalidRequestException;

public class HttpRequestParser {
    public  String getRouteKey(HttpExchange exchange) {
        var uri = exchange.getRequestURI();
        var path = uri.getPath();
        String[] parts = path.split("/");
        if (parts.length<2) {
            throw new InvalidRequestException("context not exist");
        }
        return parts[1];
    }

}
