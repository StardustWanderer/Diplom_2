package praktikum.tests;

import org.junit.After;
import praktikum.steps.UserSteps;

public abstract class BaseTest {
   protected final UserSteps userSteps = new UserSteps();

    protected String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userSteps.deleteUser(accessToken);
        }
    }
}
