package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;
import praktikum.data.UserData;
import praktikum.models.UserRegistrationModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

@Feature("Регистрация пользователя")
public class UserRegistrationTest extends BaseTest {

    @Test
    @Description("Успешная регистрация нового пользователя")
    public void shouldRegisterNewUser() {
        UserRegistrationModel userRegistrationModel = UserData.getRandomUser();

        Response response = userSteps.registerUser(userRegistrationModel);

        // Сохраняем токен, чтобы удалить пользователя после теста.
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userRegistrationModel.getEmail()))
                .body("user.name", equalTo(userRegistrationModel.getName()));

        assertNotNull("В ответе должен присутствовать accessToken", accessToken);
    }

    @Test
    @Description("Повторная регистрация уже существующего пользователя")
    public void shouldNotRegisterExistingUser() {
        UserRegistrationModel userRegistrationModel = UserData.getRandomUser();

        // Сначала регистрируем пользователя.
        Response registrationResponse = userSteps.registerUser(userRegistrationModel);

        accessToken = registrationResponse.jsonPath().getString("accessToken");

        registrationResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        // Повторно отправляем тот же набор данных.
        Response duplicateResponse = userSteps.registerUser(userRegistrationModel);

        duplicateResponse.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }
}