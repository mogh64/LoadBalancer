package org.example;

import com.sun.net.httpserver.HttpServer;
import org.example.back.Server;
import org.example.back.registery.ServiceRegistryFactory;
import org.example.common.UriSchema;
import org.example.front.request.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;


public class Main {
    private static final int PORT = 8000;
    public static void main(String[] args) throws IOException {
        registerServers();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT),0);
        server.setExecutor(null);
        server.createContext("/",new RequestHandler());
        server.start();
        System.out.println("Load balancer running on port " + PORT);
    }
    private static void registerServers() {
        Server server1 = Server.builder().schema(UriSchema.https.name()).host("bbfbf769-0cb5-4eba-b84d-4152a0949a4b.mock.pstmn.io").context("myapp").capacity(2).build();
        Server server2 = Server.builder().schema(UriSchema.https.name()).host("e146e720-4d28-40c8-b86a-a35ad1fd0596.mock.pstmn.io").context("myapp").capacity(1).build();
        ServiceRegistryFactory.getServerRegistry().add(server1);
        ServiceRegistryFactory.getServerRegistry().add(server2);
    }
}