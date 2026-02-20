import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class Steps {

    @Step("Создание пользователя")
    public static Response createUser(CreateUser user) {
        return given()
                .spec(Endpoints.req)
                .body(user)
                .when()
                .post(Endpoints.createUser);
    }

    @Step("Авторизация пользователя")
    public static Response loginUser(String token, LoginUser user) {
        return given()
                .header("Authorization", token)
                .spec(Endpoints.req)
                .body(user)
                .when()
                .post(Endpoints.loginUser);
    }

    @Step("Удаление пользователя")
    public static void deleteUser(String token) {
        given()
                .header("Authorization", token)
                .spec(Endpoints.req)
                .when()
                .delete(Endpoints.user).then().log().all();
    }

    @Step("Получение списка ингредиентов")
    public static IngredientResponse getIngredietns() {
        return given()
                .spec(Endpoints.req)
                .when()
                .get(Endpoints.ingredients)
                .as(IngredientResponse.class);
    }

    @Step("Создание заказа с авторизацией")
    public static Response createOrderWithLogin(String token, CreateOrder order) {
        return given()
                .header("Authorization", token)
                .spec(Endpoints.req)
                .body(order)
                .when()
                .post(Endpoints.order);

    }

    @Step("Создание заказа без авторизации")
    public static Response createOrderWithoutLogin(CreateOrder order) {
        return given()
                .spec(Endpoints.req)
                .body(order)
                .when()
                .post(Endpoints.order);

    }
}
