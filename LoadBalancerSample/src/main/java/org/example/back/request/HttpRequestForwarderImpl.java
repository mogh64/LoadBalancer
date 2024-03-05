package org.example.back.request;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestForwarderImpl implements HttpRequestForwarder {
    private final OkHttpClient client;
    public HttpRequestForwarderImpl() {
        this.client = new OkHttpClient();
    }
    public HttpResponse send(HttpRequest httpRequest) {

        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(httpRequest.uri().toString());

            if ("GET".equals(httpRequest.method())) {
                requestBuilder = requestBuilder.get();
            } else {
                throw new UnsupportedOperationException("Unsupported HTTP method");
            }

            Request request = requestBuilder.build();
            try (Response response = client.newCall(request).execute()) {
                Map<String, String> headers = new HashMap<>();
                for (String name : response.headers().names()) {
                    headers.put(name, response.header(name));
                }
                String responseBody = response.body().string();
                return new HttpResponse(response.code(),headers, responseBody);
            }
        } catch (Exception e) {
            e.printStackTrace();

            return new HttpResponse(HttpURLConnection.HTTP_INTERNAL_ERROR,new HashMap<>(), "Internal Server Error");
        }
    }
}
