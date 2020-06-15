import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/home", new MyHandler());
        server.createContext("/test1", new MyHandler());
        server.createContext("/test2", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "default";

            for (Method method : Routes.class.getMethods()) {
                if (method.isAnnotationPresent(WebRoute.class)) {
                    WebRoute webRoute = method.getAnnotation(WebRoute.class);
                    if (webRoute.value().equals(exchange.getRequestURI().toString())) {
                        try {
                            response = (String) method.invoke(null, exchange);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}