public class Routes {

    @WebRoute(path = "/test1")
    public static String test1() {
        return "test1";
    }

    @WebRoute(path = "/test2")
    public static String test2() {
        return "test2";
    }

    @WebRoute(path = "/test2", requestMethod = RequestMethod.POST)
    public static String test2Post() {
        return "test2 POST";
    }

    @WebRoute(path = "/user/")
    public static String getUser(String userName) {
        return "username: " + userName;
    }

}