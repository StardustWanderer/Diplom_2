package praktikum.tests;

import org.junit.After;
import praktikum.steps.UserSteps;

public abstract class BaseTest {
   protected final UserSteps userSteps = new UserSteps();

    /*
     * Здесь храним токен зарегистрированного пользователя.
     *
     * Если тест создаёт пользователя, токен нужен для его удаления
     * в методе tearDown().
     *
     * Если тест не регистрирует пользователя, значение останется null.
     */
    protected String accessToken;

    /*
     * Выполняется после каждого тестового метода.
     *
     * Если у нас есть токен, пытаемся удалить тестового пользователя.
     *
     * Это помогает не оставлять созданные аккаунты на сервере.
     * При этом очистка возможна, только если у нас есть действующий
     * токен и сервер позволяет удалить этого пользователя.
     */
    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }
}
