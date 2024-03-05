package org.example.front.response;

import com.sun.net.httpserver.HttpExchange;
import org.example.back.request.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseHandler {
    public void write(HttpExchange httpExchange, HttpResponse httpResponse) throws IOException {
        httpExchange.sendResponseHeaders(httpResponse.getStatusCode(), httpResponse.getBody().length());
        OutputStream clientResponseStream = httpExchange.getResponseBody();
        byte[] response = httpResponse.getBody().getBytes();
        clientResponseStream.write(response);
        clientResponseStream.close();
    }
}
