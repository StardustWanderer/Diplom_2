package praktikum.data;

import com.github.javafaker.Faker;
import praktikum.models.UserRegistrationModel;

import java.util.Locale;
import java.util.UUID;

public class UserData {

    public static UserRegistrationModel getRandomUser() {
        Faker faker = new Faker(Locale.ENGLISH);

        String email = generateUniqueEmail(faker);
        String password = faker.internet().password(8, 16);
        String name = faker.name().firstName();

        return new UserRegistrationModel(email, password, name);
    }

    // Генерирует случайный email без создания объекта User.
    public static String getRandomEmail() {
        Faker faker = new Faker(Locale.ENGLISH);

        return generateUniqueEmail(faker);
    }

    // Общая логика генерации уникального email.
    private static String generateUniqueEmail(Faker faker) {
        String email = faker.internet().emailAddress();
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        return email.replace("@", "-" + uniqueSuffix + "@");
    }
}