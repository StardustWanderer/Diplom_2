package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import praktikum.data.UserData;
import praktikum.models.UserLoginModel;
import praktikum.models.UserRegistrationModel;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

@Feature("Авторизация пользователя")
public class UserLoginTest extends BaseTest {

    private UserRegistrationModel registeredUser;

    @Before
    public void setUp() {
        registeredUser = UserData.getRandomUser();

        Response registrationResponse =
                userSteps.registerUser(registeredUser);

        accessToken = registrationResponse.jsonPath()
                .getString("accessToken");

        registrationResponse.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        assertNotNull(
                "Регистрация должна вернуть accessToken",
                accessToken
        );
    }

    @Test
    @Description("Успешный вход под существующим пользователем")
    public void shouldLoginExistingUser() {
        UserLoginModel credentials = new UserLoginModel(
                registeredUser.getEmail(),
                registeredUser.getPassword()
        );

        Response response = userSteps.loginUser(credentials);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body(
                        "user.email",
                        equalTo(registeredUser.getEmail())
                )
                .body(
                        "user.name",
                        equalTo(registeredUser.getName())
                );

        assertNotNull(
                "Авторизация должна вернуть accessToken",
                response.jsonPath().getString("accessToken")
        );

        assertNotNull(
                "Авторизация должна вернуть refreshToken",
                response.jsonPath().getString("refreshToken")
        );
    }

    @Test
    @Description("Ошибка входа с несуществующим email")
    public void shouldNotLoginWithIncorrectEmail() {
        UserLoginModel credentials = new UserLoginModel(
                UserData.getRandomEmail(),
                registeredUser.getPassword()
        );

        Response response = userSteps.loginUser(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("email or password are incorrect")
                );
    }

    @Test
    @Description("Ошибка входа с неверным паролем")
    public void shouldNotLoginWithIncorrectPassword() {
        UserLoginModel credentials = new UserLoginModel(
                registeredUser.getEmail(),
                "DefinitelyWrongPassword123!"
        );

        Response response = userSteps.loginUser(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo("email or password are incorrect")
                );
    }
}