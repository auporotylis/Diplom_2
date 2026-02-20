import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class CreateUserNegativeTest {

    private String email;
    private String password;
    private String name;

    @Parameterized.Parameters(name = "Email: {0}, password: {1}, name: {2}")
    public static Object[][] getData() {
        return new Object[][]{
                {"adreso4ek1" + System.currentTimeMillis() + "@yandex.ru", "1234asd", ""},
                {"adreso4ek2" + System.currentTimeMillis() + "@yandex.ru",  "", "Ivan"},
                {"", "1234asd", "Irina"},
        };
    }

    public CreateUserNegativeTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Test
    @DisplayName("Создание пользователя без любого заполненного поля")
    @Description("Проверка возникновения ошибки 403 при регистрации пользователя без заполненного email/пароля/имени")
    public void cannotCreateUserWithoutAnyFieldTest() {
        CreateUser user = new CreateUser(email, password, name);
        Steps.createUser(user)
                .then()
                .log().all()
                .statusCode(SC_FORBIDDEN)
                .body(Constant.PARAM_SUCCESS, equalTo(false))
                .body(Constant.PARAM_MESSAGE, equalTo(Constant.MESSAGE_FIELD_REQUIRED));
    }

}
