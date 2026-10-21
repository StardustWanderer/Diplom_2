package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;
import praktikum.data.UserData;
import praktikum.models.UserLoginModel;
import praktikum.models.UserRegistrationModel;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

@Feature("Авторизация пользователя")
public class UserLoginTest extends BaseTest {

    @Test
    @Description("Успешный вход под существующим пользователем")
    public void shouldLoginExistingUser() {
        // Arrange: создаём нового пользователя.
        UserRegistrationModel userRegistrationModel = UserData.getRandomUser();

        // Регистрируем пользователя.
        Response registrationResponse = userSteps.registerUser(userRegistrationModel);

        registrationResponse.then()
                .statusCode(200)
                .body("success", equalTo(true));

        // Сохраняем токен для последующего удаления пользователя.
        accessToken = registrationResponse.jsonPath().getString("accessToken");

        assertNotNull("Регистрация должна вернуть accessToken", accessToken);

        // Подготавливаем данные для авторизации.
        UserLoginModel credentials = new UserLoginModel(userRegistrationModel.getEmail(), userRegistrationModel.getPassword()
        );

        // Act: авторизуемся под созданным пользователем.
        Response loginResponse = userSteps.loginUser(credentials);

        // Assert: проверяем успешную авторизацию.
        loginResponse.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user.email", equalTo(userRegistrationModel.getEmail()))
                .body("user.name", equalTo(userRegistrationModel.getName()));

        assertNotNull("Авторизация должна вернуть accessToken", loginResponse.jsonPath().getString("accessToken"));
        assertNotNull("Авторизация должна вернуть refreshToken", loginResponse.jsonPath().getString("refreshToken"));
    }

    @Test
    @Description("Ошибка авторизации с неверными учётными данными")
    public void shouldNotLoginWithIncorrectCredentials() {
        // Создаём заведомо случайный email.
        String invalidEmail = UserData.getRandomEmail();
        UserLoginModel credentials = new UserLoginModel(invalidEmail, "WrongPassword123!");

        // Act: отправляем запрос с неверными учётными данными.
        Response response = userSteps.loginUser(credentials);

        // Assert: проверяем ожидаемую ошибку.
        response.then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}