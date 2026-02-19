import io.qameta.allure.Description;
import io.qameta.allure.junit4.AllureJunit4;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest {

    private String email;
    private String password;
    private String name;
    private String accessToken;

    @Before
    public void setUp() {
        email = "email" + System.currentTimeMillis() + "@yandex.ru";
        password = "1234pass";
        name = "Leonidas";
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            Steps.deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Заказ с авторизацией")
    @Description("Проверка успешного создания заказа с предварительной авторизацией пользователя")
    public void orderWithAuthorizationTest() {
        CreateUser createUser = new CreateUser(email, password, name);
        Response response = Steps.createUser(createUser);
        accessToken = response.path(Constant.paramAToken);

        LoginUser loginUser = new LoginUser(email, password);
        Steps.loginUser(accessToken, loginUser);

        IngredientResponse ingredientResponse =  Steps.getIngredietns();
        String ingredientId0 = ingredientResponse.getData().get(0).get_id();
        String ingredientId1 = ingredientResponse.getData().get(1).get_id();

        String[] ingredients = new String[]{ingredientId0, ingredientId1};
        CreateOrder order = new CreateOrder(ingredients);

        Steps.createOrderWithLogin(accessToken, order)
                .then().log().all()
                .statusCode(SC_OK)
                .body(Constant.paramSuccess, equalTo(true))
                .body(Constant.paramOrdIngrId, hasItem(ingredientId0))
                .body(Constant.paramOrdIngrId, hasItem(ingredientId1))
                .body(Constant.paramOrdOwnName, equalTo(name))
                .body(Constant.paramOrdOwnEmail, equalTo(email));

    }

    @Test
    @DisplayName("Заказ без авторизации")
    @Description("Проверка успешного создания заказа без предварительной авторизации пользователя")
    public void orderWithoutAuthorizationTest() {
        IngredientResponse ingredientResponse =  Steps.getIngredietns();
        String ingredientId0 = ingredientResponse.getData().get(0).get_id();
        String ingredientId1 = ingredientResponse.getData().get(1).get_id();

        String[] ingredients = new String[]{ingredientId0, ingredientId1};
        CreateOrder order = new CreateOrder(ingredients);

        Steps.createOrderWithoutLogin(order)
                .then().log().all()
                .statusCode(SC_OK)
                .body(Constant.paramSuccess, equalTo(true))
                .body(Constant.paramOrder, notNullValue());
    }

    @Test
    @DisplayName("Заказ без ингредиентов")
    @Description("Проверка возникновения ошибки 400 при заказе без ингредиентов")
    public void orderWithoutIngredientsTest() {
        CreateUser createUser = new CreateUser(email, password, name);
        Response response = Steps.createUser(createUser);
        accessToken = response.path(Constant.paramAToken);

        LoginUser loginUser = new LoginUser(email, password);
        Steps.loginUser(accessToken, loginUser);

        String[] ingredients = new String[]{};
        CreateOrder order = new CreateOrder(ingredients);

        Steps.createOrderWithLogin(accessToken, order)
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .body(Constant.paramSuccess, equalTo(false))
                .body(Constant.paramMessage, equalTo(Constant.messageIngedientsRequired));

    }

    @Test
    @DisplayName("Заказ с неправильными хешем ингредиента")
    @Description("Проверка возникновения ошибки 500 при заказе с неправильными хешем ингредиента")
    public void orderWithWrongIngredientsTest() {
        String[] ingredients = new String[]{"12345"};
        CreateOrder order = new CreateOrder(ingredients);

        Steps.createOrderWithoutLogin(order)
                .then().log().all()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
