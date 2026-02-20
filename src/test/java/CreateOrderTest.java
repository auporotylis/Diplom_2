import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

        CreateUser createUser = new CreateUser(email, password, name);
        Response response = Steps.createUser(createUser);
        accessToken = response.path(Constant.PARAM_ACCESS_TOKEN);
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
        LoginUser loginUser = new LoginUser(email, password);
        Steps.loginUser(accessToken, loginUser);

        IngredientResponse ingredientResponse =  Steps.getIngredietns();
        String firstIngredient = ingredientResponse.getData().get(0).getId();
        String secondIngredient = ingredientResponse.getData().get(1).getId();

        String[] ingredients = new String[]{firstIngredient, secondIngredient};
        CreateOrder order = new CreateOrder(ingredients);

        Steps.createOrderWithLogin(accessToken, order)
                .then().log().all()
                .statusCode(SC_OK)
                .body(Constant.PARAM_SUCCESS, equalTo(true))
                .body(Constant.PARAM_ORD_INGR_ID, hasItem(firstIngredient))
                .body(Constant.PARAM_ORD_INGR_ID, hasItem(secondIngredient))
                .body(Constant.PARAM_ORD_OWN_NAME, equalTo(name))
                .body(Constant.PARAM_ORD_OWN_EMAIL, equalTo(email));

    }

    @Test
    @DisplayName("Заказ без авторизации")
    @Description("Проверка успешного создания заказа без предварительной авторизации пользователя")
    public void orderWithoutAuthorizationTest() {
        IngredientResponse ingredientResponse =  Steps.getIngredietns();
        String firstIngredient = ingredientResponse.getData().get(0).getId();
        String secondIngredient = ingredientResponse.getData().get(1).getId();

        String[] ingredients = new String[]{firstIngredient, secondIngredient};
        CreateOrder order = new CreateOrder(ingredients);

        Steps.createOrderWithoutLogin(order)
                .then().log().all()
                .statusCode(SC_OK)
                .body(Constant.PARAM_SUCCESS, equalTo(true))
                .body(Constant.PARAM_ORDER, notNullValue());
    }

    @Test
    @DisplayName("Заказ без ингредиентов")
    @Description("Проверка возникновения ошибки 400 при заказе без ингредиентов")
    public void orderWithoutIngredientsTest() {
        LoginUser loginUser = new LoginUser(email, password);
        Steps.loginUser(accessToken, loginUser);

        String[] ingredients = new String[]{};
        CreateOrder order = new CreateOrder(ingredients);

        Steps.createOrderWithLogin(accessToken, order)
                .then().log().all()
                .statusCode(SC_BAD_REQUEST)
                .body(Constant.PARAM_SUCCESS, equalTo(false))
                .body(Constant.PARAM_MESSAGE, equalTo(Constant.MESSAGE_INGREDIENTS_REQUIRED));

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
