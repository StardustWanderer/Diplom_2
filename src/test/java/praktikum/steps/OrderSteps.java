package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.models.OrderModel;

import static io.restassured.RestAssured.given;
import static praktikum.config.ApiEndpoints.INGREDIENTS_PATH;
import static praktikum.config.ApiEndpoints.ORDERS_PATH;

public class OrderSteps extends BaseSteps {

    @Step("Получить список ингредиентов")
    public Response getIngredients() {
        return given()
                .spec(getRequestSpecification())
                .log().all()
                .when()
                .get(INGREDIENTS_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    @Step("Создать заказ с авторизацией")
    public Response createOrder(
            OrderModel orderModel,
            String accessToken
    ) {
        return given()
                .spec(getRequestSpecification())
                .header("Authorization", accessToken)
                .body(serializeToJson(orderModel))
                .log().all()
                .when()
                .post(ORDERS_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    @Step("Создать заказ без авторизации")
    public Response createOrderWithoutAuth(OrderModel orderModel) {
        return given()
                .spec(getRequestSpecification())
                .body(serializeToJson(orderModel))
                .log().all()
                .when()
                .post(ORDERS_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }
}