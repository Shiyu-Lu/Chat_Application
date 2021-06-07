package BLL;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

//import sun.net.www.protocol.http.HttpURLConnection;

public class ChatHttpServer {

    Map<String,ChatClientForUser> userMap;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8443), 0);
        server.createContext("/api/login", new MyHandler());
        server.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("aaa");


            /* --------- NEW --------- */
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
                t.getResponseHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token");
                t.sendResponseHeaders(204, -1);
                return;
            }

            // Write here the code to GET requests
            String requestMethod = t.getRequestMethod();
            System.out.println(requestMethod);
            if ("POST".equalsIgnoreCase(requestMethod)) {
                // 打印输入流
                InputStream requestBody = t.getRequestBody();
                String s;
                try (requestBody) {
                    int n;
                    StringBuilder sb = new StringBuilder();
                    while ((n = requestBody.read()) != -1) {
                        sb.append((char) n);
                    }
                    s = sb.toString();
                }
                System.out.println(s);
                // 获取响应头，接下来我们来设置响应头信息
                Headers responseHeaders = t.getResponseHeaders();
                // 以json形式返回，其他还有text/html等等
                responseHeaders.set("Content-Type", "application/json");
                // 设置响应码200和响应body长度，这里我们设0，没有响应体
                t.sendResponseHeaders(200, 0);
                // 获取响应体
                OutputStream responseBody = t.getResponseBody();
                // 获取请求头并打印
                Headers requestHeaders = t.getRequestHeaders();
                Set<String> keySet = requestHeaders.keySet();
                Iterator<String> iter = keySet.iterator();
                String strRequestHeaders = "";
                while (iter.hasNext()) {
                    String key = iter.next();
                    List values = requestHeaders.get(key);
                    strRequestHeaders = key + " = " + values.toString() + "\r\n";
                }
                System.out.println(strRequestHeaders);
                // 关闭输出流
                String response = "{\"code\":\"200\"}";
                System.out.println(response);
                responseBody.write(response.getBytes());
                responseBody.close();
            }
            /* --------- NEW --------- */

//          String response = "{\"code\":\"200\"}";
//          String response = "hello world";
//          URI url = t.getRequestURI();
//          InputStream requestBody = t.getRequestBody();
//          System.out.println(url);
//          t.sendResponseHeaders(200, 0);
//          OutputStream os = t.getResponseBody();
//          os.write(response.getBytes());
//          os.close();
        }
    }

}

class Result{
    public int code;
    public Result(int code){
        this.code = code;
    }
}