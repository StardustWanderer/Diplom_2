package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.data.UserData;
import praktikum.models.UserRegistrationModel;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
@Feature("Регистрация без обязательных полей")
public class UserRegistrationRequiredFieldsTest extends BaseTest {

    private final String missingField;

    public UserRegistrationRequiredFieldsTest(String missingField) {
        this.missingField = missingField;
    }

    @Parameterized.Parameters(
            name = "Отсутствует обязательное поле: {0}"
    )
    public static Object[][] testData() {
        return new Object[][]{
                {"email"},
                {"password"},
                {"name"}
        };
    }

    @Test
    @Description("Регистрация без одного из обязательных полей")
    public void shouldNotRegisterUserWithoutRequiredField() {
        UserRegistrationModel user = UserData.getRandomUser();

        // Устанавливаем в null именно то поле,
        // которое должно отсутствовать в JSON.
        switch (missingField) {
            case "email":
                user.setEmail(null);
                break;
            case "password":
                user.setPassword(null);
                break;
            case "name":
                user.setName(null);
                break;
            default:
                throw new IllegalArgumentException(
                        "Неизвестное обязательное поле: " + missingField
                );
        }

        Response response = userSteps.registerUser(user);

        // Если токен неожиданно вернулся, сохраняем его
        // для возможной очистки в @After.
        accessToken = response.jsonPath()
                .getString("accessToken");

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body(
                        "message",
                        equalTo(
                                "Email, password and name are required fields"
                        )
                );
    }
}