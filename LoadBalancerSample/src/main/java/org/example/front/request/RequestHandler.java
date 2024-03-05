package org.example.front.request;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.back.request.HttpRequestForwarder;
import org.example.back.request.HttpRequestForwarderImpl;
import org.example.back.registery.Server;
import org.example.back.request.HttpResponse;
import org.example.front.Configuration;
import org.example.front.LoadBalancer;
import org.example.front.response.ResponseHandler;

import java.io.IOException;
import java.net.http.HttpRequest;

public class RequestHandler implements HttpHandler {

    private final HttpRequestParser requestParser;
    private final LoadBalancer loadBalancer;
    private final HttpRequestBuilder requestBuilder;
    private final HttpRequestForwarder requestForwarder;
    private final ResponseHandler responseHandler;
    public RequestHandler() {
         requestParser = new  HttpRequestParser();
         loadBalancer = new LoadBalancer(Configuration.getAlgorithmType());
         requestBuilder = new HttpRequestBuilder();
         requestForwarder = new HttpRequestForwarderImpl();
         responseHandler = new ResponseHandler();
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        // fetch route key
        var routeKey = requestParser.getRouteKey(httpExchange);
        //find destination server
        Server destinationServer =  loadBalancer.getDestinationServer(routeKey);
        // create http request to destination server
        HttpRequest serverRequest = requestBuilder.createHttpRequest(httpExchange,destinationServer);
        //send http request to destination
        HttpResponse httpResponse = requestForwarder.send(serverRequest);
        // write response back to the client connection
        responseHandler.write(httpExchange, httpResponse);
    }

}
