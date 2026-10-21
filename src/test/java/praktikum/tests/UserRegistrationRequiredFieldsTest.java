package praktikum.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.data.UserData;
import praktikum.models.UserRegistrationModel;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
@Feature("Регистрация без обязательных полей")
public class UserRegistrationRequiredFieldsTest extends BaseTest{

    private final String missingField;

    public UserRegistrationRequiredFieldsTest(String missingField) {
        this.missingField = missingField;
    }

    @Parameterized.Parameters(name = "Отсутствует обязательное поле: {0}")
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
        UserRegistrationModel userRegistrationModel = UserData.getRandomUser();

        // Формируем тело запроса из всех обязательных полей.
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("email", userRegistrationModel.getEmail());
        requestBody.put("password", userRegistrationModel.getPassword());
        requestBody.put("name", userRegistrationModel.getName());

        // Убираем одно поле в зависимости от параметра теста.
        requestBody.remove(missingField);

        Response response = userSteps.registerUser(requestBody);

        // Если сервер неожиданно вернул токен,
        // сохраним его до проверок, которые могут упасть.
        accessToken = response.jsonPath().getString("accessToken");

        response.then()
                .statusCode(403)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }
}