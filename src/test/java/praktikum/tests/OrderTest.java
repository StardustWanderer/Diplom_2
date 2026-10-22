package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import praktikum.data.UserData;
import praktikum.models.OrderModel;
import praktikum.models.UserRegistrationModel;
import praktikum.steps.OrderSteps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@Feature("Создание заказа")
public class OrderTest extends BaseTest {

    private final OrderSteps orderSteps = new OrderSteps();

    @Before
    public void setUp() {
        UserRegistrationModel user = UserData.getRandomUser();

        Response registrationResponse = userSteps.registerUser(user);

        // Сохраняем токен до проверок, чтобы @After мог
        // попытаться удалить пользователя, если проверка упадёт.
        accessToken = registrationResponse.jsonPath()
                .getString("accessToken");

        registrationResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        assertNotNull(
                "При регистрации должен быть получен accessToken",
                accessToken
        );
    }

    // Получаем два корректных ID ингредиентов из API.
    private List<String> getIngredientIds() {
        Response response = orderSteps.getIngredients();

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        List<String> ids = response.jsonPath()
                .getList("data._id", String.class);

        assertNotNull("Список ID ингредиентов не должен быть null", ids);
        assertTrue(
                "API должно вернуть минимум два ингредиента",
                ids.size() >= 2
        );

        return List.of(ids.get(0), ids.get(1));
    }

    @Test
    @Description("Создание заказа с авторизацией и ингредиентами")
    public void shouldCreateOrderWithAuthorization() {
        List<String> ingredientIds = getIngredientIds();
        OrderModel orderModel = new OrderModel(ingredientIds);

        Response response =
                orderSteps.createOrder(orderModel, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без авторизации")
    public void shouldCreateOrderWithoutAuthorization() {
        List<String> ingredientIds = getIngredientIds();
        OrderModel orderModel = new OrderModel(ingredientIds);

        // Запрос намеренно отправляется без заголовка Authorization.
        Response response =
                orderSteps.createOrderWithoutAuth(orderModel);

        // Ожидаемый результат согласован с куратором.
        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void shouldNotCreateOrderWithoutIngredients() {
        OrderModel orderModel =
                new OrderModel(Collections.emptyList());

        Response response =
                orderSteps.createOrder(orderModel, accessToken);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("Ingredient ids must be provided")
                );
    }

    @Test
    @Description("Создание заказа с неверным хешем ингредиента")
    public void shouldNotCreateOrderWithInvalidIngredientHash() {
        List<String> ingredientIds =
                new ArrayList<>(getIngredientIds());

        // Оставляем первый настоящий ID, второй заменяем
        // на некорректный.
        ingredientIds.set(1, "invalid-ingredient-hash");

        OrderModel orderModel = new OrderModel(ingredientIds);

        Response response =
                orderSteps.createOrder(orderModel, accessToken);

        // Ожидаемый статус 400 — по текущему указанию куратора.
        response.then()
                .statusCode(SC_BAD_REQUEST);
    }
}