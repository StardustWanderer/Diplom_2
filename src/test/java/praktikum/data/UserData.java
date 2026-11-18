package praktikum.data;

import com.github.javafaker.Faker;
import praktikum.models.UserRegistrationModel;

import java.util.Locale;

public class UserData {
    private static final Faker userRus = new Faker(new Locale("ru"));
    private static final Faker userEng = new Faker(Locale.ENGLISH);


    public static UserRegistrationModel getRandomUser() {

        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateFirstName();

        return new UserRegistrationModel(email, password, name);
    }

    // Генерирует случайный email без создания объекта User.
    public static String getRandomEmail() {
        return generateUniqueEmail();
    }

    private static String generateUniqueEmail() {
        String email = userEng.internet().emailAddress();
        String uniqueSuffix = userEng.regexify("[0-9]{6}");

        return email.replace("@", "-" + uniqueSuffix + "@");
    }

    private static String generateUniquePassword() {
        return userEng.internet().password(8, 16);
    }

    private static String generateFirstName() {
        return userRus.name().firstName();
    }
}