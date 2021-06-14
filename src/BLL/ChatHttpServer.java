package BLL;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class ChatHttpServer {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8443), 0);
        server.createContext("/api/login", new MyHandler());
        server.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            BufferedReader in = new BufferedReader(new InputStreamReader(t.getRequestBody()));
            System.out.println(in.readLine());
            Headers temp = t.getResponseHeaders();
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}