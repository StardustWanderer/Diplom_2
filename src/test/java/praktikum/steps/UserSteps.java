package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.models.UserLoginModel;
import praktikum.models.UserRegistrationModel;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static praktikum.config.ApiEndpoints.*;

public class UserSteps extends BaseSteps {

    /*
     * Регистрирует пользователя, переданного в виде модели User.
     *
     * Этот метод используется для обычной регистрации,
     * когда у нас есть email, password и name.
     *
     * @Step отображает действие как шаг в отчёте Allure.
     */
    @Step("Зарегистрировать пользователя")
    public Response registerUser(UserRegistrationModel userRegistrationModel) {

        // Передаём объект UserRegistrationModel в общий метод отправки запроса.
        return sendRegistrationRequest(userRegistrationModel);
    }

    /*
     * Перегруженный метод регистрации.
     *
     * Используется, когда нужно самостоятельно управлять
     * JSON-телом запроса, например удалить из него email.
     *
     * В тесте мы создаём Map из ключей и значений,
     * а затем передаём её в этот метод.
     */
    @Step("Зарегистрировать пользователя с заданным телом запроса")
    public Response registerUser(Map<String, String> requestBody) {
        return sendRegistrationRequest(requestBody);
    }

    /*
     * Общая логика отправки запроса регистрации.
     *
     * Этот приватный метод нужен, чтобы не дублировать
     * один и тот же HTTP-запрос в двух перегруженных методах.
     *
     * Object позволяет передать сюда как модель User,
     * так и Map<String, String>.
     *
     * При этом снаружи мы сохраняем более понятные типы:
     * registerUser(User) и registerUser(Map<String, String>).
     */
    private Response sendRegistrationRequest(Object requestBody) {
        return given()

                // Применяем общие настройки HTTP-запроса.
                .spec(getRequestSpecification())

                // Выводим сведения об исходящем запросе в консоль.
                .log().all()

                // Устанавливаем тело запроса.
                .body(requestBody)

                // Переходим к отправке запроса.
                .when()

                // Отправляем POST на эндпоинт регистрации.
                .post(REGISTER_PATH)

                // Работаем с полученным ответом.
                .then()

                // Выводим сведения об ответе сервера.
                .log().all()

                /*
                 * После then() мы находимся в цепочке
                 * проверки ответа.
                 *
                 * extract().response() возвращает исходный
                 * объект Response, чтобы тест мог проверять
                 * статус и извлекать значения из JSON.
                 */
                .extract()
                .response();
    }

    /*
     * Авторизует существующего пользователя.
     *
     * В теле запроса передаются email и password,
     * которые хранятся в модели UserCredentials.
     *
     * Возвращаем Response, потому что тесту могут понадобиться
     * статус ответа, данные пользователя и токены.
     */
    @Step("Авторизовать пользователя")
    public Response loginUser(UserLoginModel credentials) {
        return given()
                .spec(getRequestSpecification())
                .log().all()

                // Передаём email и password в теле запроса.
                .body(credentials)

                .when()
                .post(LOGIN_PATH)

                // Логируем ответ и возвращаем Response.
                .then()
                .log().all()
                .extract()
                .response();
    }

    /*
     * Удаляет пользователя.
     *
     * Метод используется в @After, чтобы очистить тестовые
     * данные после теста.
     *
     * accessToken приходит из ответа регистрации или авторизации.
     * В Stellar Burgers он уже содержит префикс Bearer,
     * поэтому здесь не нужно добавлять его повторно.
     */
    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .spec(getRequestSpecification())
                .log().all()

                // Передаём токен в заголовке авторизации.
                .header("Authorization", accessToken)

                .when()
                .delete(USER_PATH)

                .then()
                .log().all()
                .extract()
                .response();
    }
}