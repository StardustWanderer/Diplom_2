package praktikum.steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.models.UserLoginModel;
import praktikum.models.UserRegistrationModel;

import static io.restassured.RestAssured.given;
import static praktikum.config.ApiEndpoints.*;

public class UserSteps extends BaseSteps {

    @Step("Зарегистрировать пользователя")
    public Response registerUser(
            UserRegistrationModel userRegistrationModel
    ) {
        return given()
                .spec(getRequestSpecification())
                .body(serializeToJson(userRegistrationModel))
                .log().all()
                .when()
                .post(REGISTER_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    @Step("Авторизовать пользователя")
    public Response loginUser(UserLoginModel credentials) {
        return given()
                .spec(getRequestSpecification())
                .body(serializeToJson(credentials))
                .log().all()
                .when()
                .post(LOGIN_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }

    @Step("Удалить пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .spec(getRequestSpecification())
                .header("Authorization", accessToken)
                .log().all()
                .when()
                .delete(USER_PATH)
                .then()
                .log().all()
                .extract()
                .response();
    }
}