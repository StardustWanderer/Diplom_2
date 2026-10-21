package praktikum.steps;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static praktikum.config.Urls.BASE_URL;

public abstract class BaseSteps {
    protected RequestSpecification getRequestSpecification() {
        return new RequestSpecBuilder()

                // Устанавливаем базовый адрес сервера.
                .setBaseUri(BASE_URL)

                // Указываем, что тело запроса отправляется в JSON.
                .setContentType(ContentType.JSON)

                /*
                 * Подключаем интеграцию с Allure.
                 * Она позволяет прикладывать информацию
                 * об HTTP-запросах и ответах к отчёту.
                 */
                .addFilter(new AllureRestAssured())
                .build();
    }
}
