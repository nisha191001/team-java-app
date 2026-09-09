package com.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class App {

    public static void main(String[] args) throws IOException {

        HttpServer server =
                HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", App::handleRequest);

        server.setExecutor(null);

        System.out.println("Team Java App started on port 8080");

        server.start();
    }

    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        String response =
                "Hello from Team Java App - Version 1.0";

        exchange.sendResponseHeaders(
                200,
                response.getBytes().length
        );

        OutputStream outputStream =
                exchange.getResponseBody();

        outputStream.write(response.getBytes());

        outputStream.close();
    }
}
