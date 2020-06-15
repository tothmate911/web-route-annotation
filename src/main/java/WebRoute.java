import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface WebRoute {
    String path();
    RequestMethod requestMethod() default RequestMethod.GET;
}
