import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Test {
    private static Map<String, Method> pathMethodPairs = new HashMap<>();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        for (Method method : Routes.class.getMethods()) {
            if (method.isAnnotationPresent(WebRoute.class)) {
                WebRoute webRoute = method.getAnnotation(WebRoute.class);
                String path = webRoute.value();
                pathMethodPairs.put(path, method);
                server.createContext(path, new MyHandler());
            }
        }
        server.setExecutor(null);
        server.start();
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "default response";

            Method method = pathMethodPairs.get(exchange.getRequestURI().toString());
            try {
                response = (String) method.invoke(null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}