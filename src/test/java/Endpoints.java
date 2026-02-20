import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;

public class Endpoints {
    public static String baseURI = "https://stellarburgers.education-services.ru";
    public static String createUser = "/api/auth/register";
    public static String user = "/api/auth/user";
    public static String loginUser = "/api/auth/login";
    public static String order = "/api/orders";
    public static String ingredients = "/api/ingredients";


    public static RequestSpecification req = new RequestSpecBuilder()
            .setBaseUri(baseURI)
            .setContentType("application/json")
            .log(LogDetail.ALL)
            .build();
}
