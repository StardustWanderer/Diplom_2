package praktikum.steps;

import com.google.gson.Gson;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static praktikum.config.Urls.BASE_URL;

public abstract class BaseSteps {

    private final Gson gson = new Gson();

    // Преобразует Java-объект в JSON-строку.
    protected String serializeToJson(Object object) {
        return gson.toJson(object);
    }

    // Общие настройки HTTP-запросов.
    protected RequestSpecification getRequestSpecification() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }
}