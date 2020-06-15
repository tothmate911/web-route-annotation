import com.sun.net.httpserver.HttpExchange;

public class Routes {

    @WebRoute("/test1")
    public static String test1() {
        return "test1";
    }

    @WebRoute("/test2")
    public static String test2() {
        return "test2";
    }
}