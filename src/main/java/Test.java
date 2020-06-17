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
    private static Map<String, Map<RequestMethod, Method>> pathMethodPairs = new HashMap<>();

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        createContexts(server);
        server.setExecutor(null);
        server.start();
    }

    private static void createContexts(HttpServer server) {
        for (Method method : Routes.class.getMethods()) {
            if (method.isAnnotationPresent(WebRoute.class)) {
                WebRoute webRoute = method.getAnnotation(WebRoute.class);
                RequestMethod requestMethod = webRoute.requestMethod();
                String path = webRoute.path();

                if (pathMethodPairs.get(path) == null) {
                    Map<RequestMethod, Method> requestMethodMethodPair = new HashMap<RequestMethod, Method>() {{
                        put(requestMethod, method);
                    }};
                    pathMethodPairs.put(path, requestMethodMethodPair);
                } else {
                    pathMethodPairs.get(path).put(requestMethod, method);
                }

                server.createContext(webRoute.path(), new MyHandler());
            }
        }
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "default response";

            RequestMethod requestMethod = RequestMethod.valueOf(exchange.getRequestMethod());
            String path = exchange.getRequestURI().getPath();
            Method method = pathMethodPairs.get(path).get(requestMethod);

            String query = exchange.getRequestURI().getQuery();

            try {
                if (query == null) {
                    response = (String) method.invoke(null);
                } else {
                    Map<String, String> queryMap = queryToMap(query);
                    String userName = queryMap.get("username");
                    response = (String) method.invoke(null, userName);
                }

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private Map<String, String> queryToMap(String query) {
            Map<String, String> queryMap = new HashMap<>();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                queryMap.put(pair[0], pair[1]);
            }
            return queryMap;
        }
    }
}