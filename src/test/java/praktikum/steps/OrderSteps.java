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

    /*
     * Создаёт заказ от имени авторизованного пользователя.
     *
     * Принимает два аргумента:
     *
     * orderModel — тело заказа со списком ингредиентов;
     * accessToken — токен пользователя.
     *
     * Тело и токен передаются разными способами:
     * ингредиенты идут в JSON, токен — в заголовке Authorization.
     */
    @Step("Создать заказ с авторизацией")
    public Response createOrder(OrderModel orderModel, String accessToken) {
        return given()
                .spec(getRequestSpecification())
                .log().all()

                // Передаём токен, подтверждающий авторизацию.
                .header("Authorization", accessToken)

                // Передаём список ингредиентов в JSON.
                .body(orderModel)

                .when()
                .post(ORDERS_PATH)

                .then()
                .log().all()
                .extract()
                .response();
    }

    /*
     * Создаёт заказ без авторизации.
     *
     * В отличие от createOrder(), здесь нет заголовка
     * Authorization.
     *
     * Метод нужен для проверки того, что сервер запрещает
     * создавать заказ без токена.
     */
    @Step("Создать заказ без авторизации")
    public Response createOrderWithoutAuth(OrderModel orderModel) {
        return given()
                .spec(getRequestSpecification())
                .log().all()

                // Намеренно не добавляем Authorization.

                // Тело заказа при этом присутствует.
                .body(orderModel)

                .when()
                .post(ORDERS_PATH)

                .then()
                .log().all()
                .extract()
                .response();
    }
}
