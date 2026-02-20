import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {
    private String email;
    private String password;
    private String name;
    private String accessToken;

    private CreateUser createUser;
    private LoginUser loginUser;

    @Before
    public void setUp() {
        email = "adreso4ek" + System.currentTimeMillis() + "@yandex.ru";
        password = "1234qwe";
        name = "Ivetti";

        createUser = new CreateUser(email, password, name);
        Response response = Steps.createUser(createUser)
                .then()
                .log().all().extract().response();

        accessToken = response.path(Constant.PARAM_ACCESS_TOKEN);
        response.then().statusCode(SC_OK);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Steps.deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Логин пользователя")
    @Description("Проверка успешного входа пользователя в систему")
    public void loginCourierTest() {
        loginUser = new LoginUser(email, password);
        Steps.loginUser(accessToken, loginUser)
                .then()
                .log().all()
                .statusCode(SC_OK)
                .body(Constant.PARAM_SUCCESS, equalTo(true))
                .body(Constant.PARAM_USER_EMAIL, equalTo(email))
                .body(Constant.PARAM_USER_NAME, equalTo(name));
    }

    @Test
    @DisplayName("Невозможно авторизоваться с неверным email")
    @Description("Проверка возникновения ошибки 401 при попытке авторизоваться с неверным email")
    public void cannotLoginWithWrongLoginTest() {
        loginUser = new LoginUser(email + "wrong", password);
        Steps.loginUser(accessToken, loginUser)
                .then()
                .log().all()
                .statusCode(SC_UNAUTHORIZED)
                .body(Constant.PARAM_SUCCESS, equalTo(false))
                .body(Constant.PARAM_MESSAGE, equalTo(Constant.MESSAGE_FIELD_INSORRECT));
    }

    @Test
    @DisplayName("Невозможно авторизоваться с неверным паролем")
    @Description("Проверка возникновения ошибки 401 при попытке авторизоваться с неверным паролем")
    public void cannotLoginWithWrongPasswordTest() {
        loginUser = new LoginUser(email, password + "wrong");
        Steps.loginUser(accessToken, loginUser)
                .then()
                .log().all()
                .statusCode(SC_UNAUTHORIZED)
                .body(Constant.PARAM_SUCCESS, equalTo(false))
                .body(Constant.PARAM_MESSAGE, equalTo(Constant.MESSAGE_FIELD_INSORRECT));
    }
}
