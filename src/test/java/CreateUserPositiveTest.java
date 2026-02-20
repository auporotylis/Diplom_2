import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateUserPositiveTest {

    private String email;
    private String password;
    private String name;

    private String accessToken;

    @Before
    public void setUp() {
        if (email == null && password == null && name == null) {
            email = "adreso4ek" + System.currentTimeMillis() + "@yandex.ru";
            password = "1234asd";
            name = "Dasha";
        }
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Steps.deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Проверка успешного создания уникального пользователя")
    public void createUserTest() {
        CreateUser user = new CreateUser(email, password, name);
        Response response = Steps.createUser(user)
                .then()
                .log().all().extract().response();

        accessToken = response.path(Constant.PARAM_ACCESS_TOKEN);

        response.then().statusCode(SC_OK)
                .body(Constant.PARAM_SUCCESS, equalTo(true))
                .body(Constant.PARAM_USER_EMAIL, equalTo(email))
                .body(Constant.PARAM_USER_NAME, equalTo(name));


    }

    @Test
    @DisplayName("Невозможно создать дубликат пользователя")
    @Description("Проверка возникновения ошибки 403 при попытке создать пользователя, котороый уже зарегистрирован ")
    public void cannotCreateDuplicateUserTest() {
        CreateUser user = new CreateUser(email, password, name);
        CreateUser user2 = new CreateUser(email, password, name);

        Response response = Steps.createUser(user)
                .then()
                .log().all()
                .statusCode(SC_OK).extract().response();

        accessToken = response.path(Constant.PARAM_ACCESS_TOKEN);

        response.then().body(Constant.PARAM_SUCCESS, equalTo(true))
                .body(Constant.PARAM_USER_EMAIL, equalTo(email))
                .body(Constant.PARAM_USER_NAME, equalTo(name));


        Steps.createUser(user2)
                .then()
                .log().all()
                .statusCode(SC_FORBIDDEN)
                .body(Constant.PARAM_SUCCESS, equalTo(false))
                .body(Constant.PARAM_MESSAGE, equalTo(Constant.MESSAGE_USER_EXISTS));

    }
}
