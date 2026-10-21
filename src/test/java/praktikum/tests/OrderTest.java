package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;

import org.junit.Test;

import praktikum.data.UserData;
import praktikum.models.OrderModel;
import praktikum.models.UserRegistrationModel;
import praktikum.steps.OrderSteps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/*
 * Набор тестов для эндпоинта POST /api/orders.
 *
 * Проверяем:
 * 1. Создание заказа с авторизацией.
 * 2. Создание заказа без авторизации.
 * 3. Создание заказа без ингредиентов.
 * 4. Создание заказа с неверным ID ингредиента.
 */
@Feature("Создание заказа")
public class OrderTest extends BaseTest{

    /*
     * Объект для выполнения HTTP-запросов.
     *
     * Он создаётся один раз при создании экземпляра тестового класса.
     * JUnit 4 обычно создаёт новый экземпляр тестового класса
     * для каждого тестового метода.
     */
    private final OrderSteps orderSteps = new OrderSteps();

    /*
     * Вспомогательный метод для получения двух ID ингредиентов.
     *
     * Почему мы не прописываем ID вручную?
     * Потому что лучше получать актуальные идентификаторы
     * от самого API, а не полагаться на значения из примера.
     *
     * Этот метод используется в тестах, которым нужны
     * существующие ингредиенты.
     */
    private List<String> getIngredientIds() {

        // Отправляем GET /api/ingredients.
        Response response = orderSteps.getIngredients();

        // Проверяем, что сервер успешно вернул список ингредиентов.
        response.then().
                statusCode(200)
                .body("success", equalTo(true));

        // Получаем список ID всех ингредиентов.
        List<String> ids = response.jsonPath().getList("data._id", String.class);

        // Убеждаемся, что список существует.
        assertNotNull("Список ID ингредиентов не должен быть null", ids);

        // Для нашего заказа нужны минимум два ингредиента.
        assertTrue("В списке должно быть не менее двух ингредиентов", ids.size() >= 2);

        /*
         * Возвращаем только первые два ID.
         */
        return List.of(ids.get(0), ids.get(1));
    }

    /*
     * ТЕСТ 1.
     *
     * Проверяем успешное создание заказа с авторизацией
     * и существующими ингредиентами.
     *
     * Этот тест покрывает сразу два условия задания:
     * заказ с токеном и заказ с валидными ингредиентами.
     */
    @Test
    @Description("Создание заказа с авторизацией и ингредиентами")
    public void shouldCreateOrderWithAuthorization() {

        // ШАГ 1. Генерируем данные нового пользователя.
        UserRegistrationModel userRegistrationModel = UserData.getRandomUser();

        /*
         * ШАГ 2. Регистрируем пользователя.
         *
         * При успешной регистрации сервер возвращает токены,
         * включая accessToken.
         *
         * Отдельно вызывать loginUser() здесь не требуется:
         * accessToken возвращается уже при регистрации.
         */
        Response registrationResponse = userSteps.registerUser(userRegistrationModel);

        /*
         * Сразу сохраняем токен, чтобы @After мог попробовать
         * удалить пользователя даже в случае последующей ошибки
         * проверок регистрации.
         */
        accessToken = registrationResponse
                .jsonPath()
                .getString("accessToken");

        // Проверяем, что регистрация прошла успешно.
        registrationResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        // Проверяем, что сервер вернул токен.
        assertNotNull("При регистрации должен быть получен accessToken", accessToken);

        // ШАГ 3. Получаем реальные ID ингредиентов.
        List<String> ingredientIds = getIngredientIds();

        /*
         * ШАГ 4. Создаём модель тела заказа.
         *
         * REST Assured сериализует этот объект в JSON
         * и отправит его в теле POST-запроса.
         */
        OrderModel orderModel = new OrderModel(ingredientIds);

        /*
         * ШАГ 5. Отправляем запрос с токеном.
         *
         * Внутри createOrder() accessToken попадёт в заголовок
         * Authorization, а orderRequest — в тело запроса.
         */
        Response response = orderSteps.createOrder(orderModel, accessToken);

        // ШАГ 6. Проверяем ответ сервера.
        response.then()
                .statusCode(200)
                .body("success", equalTo(true))

                // Успешно созданному заказу должен быть присвоен номер.
                .body("order.number", notNullValue());
    }

    /*
     * ТЕСТ 2.
     *
     * Проверяем, что сервер не позволяет создать заказ
     * без заголовка Authorization.
     *
     * Пользователя специально не регистрируем:
     * нам нужно проверить именно запрос без токена.
     */
    @Test
    @Description("Создание заказа без авторизации")
    public void shouldCreateOrderWithoutAuthorization() {

        /*
         * Ингредиенты нужны, чтобы тело запроса было корректным.
         *
         * Иначе мы могли бы получить ошибку из-за тела запроса,
         * а не из-за отсутствия авторизации.
         */
        List<String> ingredientIds = getIngredientIds();

        // Формируем тело заказа с валидными ингредиентами.
        OrderModel orderModel = new OrderModel(ingredientIds);

        /*
         * Вызываем метод, который не добавляет заголовок
         * Authorization.
         */
        Response response = orderSteps.createOrderWithoutAuth(orderModel);

        /*
         *УТОЧНИЛ У КУРАТОРА - 200 OK является валидныйм
         * ожидаемым результатом
         */

        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void shouldNotCreateOrderWithoutIngredients() {

        // ШАГ 1. Регистрируем пользователя.
        UserRegistrationModel userRegistrationModel = UserData.getRandomUser();

        Response registrationResponse = userSteps.registerUser(userRegistrationModel);

        // Сохраняем токен для авторизованного запроса и очистки.
        accessToken = registrationResponse
                .jsonPath()
                .getString("accessToken");

        registrationResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        assertNotNull("При регистрации должен быть получен accessToken", accessToken);

        /*
         * ШАГ 2. Создаём заказ с пустым списком.
         */
        OrderModel orderModel = new OrderModel(Collections.emptyList());

        // ШАГ 3. Отправляем заказ с токеном.
        Response response = orderSteps.createOrder(orderModel, accessToken);

        // ШАГ 4. Проверяем ожидаемую ошибку валидации.
        response.then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }


    @Test
    @Description("Создание заказа с неверным хешем ингредиента")
    public void shouldNotCreateOrderWithInvalidIngredientHash() {

        // ШАГ 1. Регистрируем пользователя.
        UserRegistrationModel userRegistrationModel = UserData.getRandomUser();

        Response registrationResponse = userSteps.registerUser(userRegistrationModel);

        // Сохраняем токен для запроса и последующей очистки.
        accessToken = registrationResponse
                .jsonPath()
                .getString("accessToken");

        registrationResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        assertNotNull("При регистрации должен быть получен accessToken", accessToken);

        // ШАГ 2. Получаем валидные ID ингредиентов.
        //обернули иммутабельный List.of в ArrayList
        List<String> ingredientIds = new ArrayList<>(getIngredientIds());

        // Заменяем второй валидный ID на невалидный.
        ingredientIds.set(1, "invalid-ingredient-hash");

        // Формируем тело заказа с одним невалидным ID ингредиента.
        OrderModel orderModel = new OrderModel(ingredientIds);

        // ШАГ 3. Отправляем запрос с авторизацией.
        Response response = orderSteps.createOrder(orderModel, accessToken);

        /*
         * ШАГ 4. Проверяем ожидаемый статус.
         *
         * УТОЧНИЛ У КУРАТОРА - при невалидном хеше ОЖИДАЕМ,
         * что сервер возвращает 400 Bad Request.
         */
        response.then()
                .statusCode(400);
    }
}