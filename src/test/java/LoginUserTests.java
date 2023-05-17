import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class LoginUserTests {

    private UserClient userClient;
    private User user;
    private ValidatableResponse response;
    private String accessToken;
    private String refreshToken;

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userClient = new UserClient();
        response = userClient.createUser(user);
    }

    @After
    public void cleanUp() {
        if (response != null) {
            accessToken = response.extract().path("accessToken");
            userClient.deleteUser(accessToken.split(" ")[1]);
        }
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем")
    public void loginUserByGoodCredentialsShowsTrue() {

        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));

        int statusCode = loginResponse.extract().statusCode();
        boolean isSuccess = loginResponse.extract().path("success");
        refreshToken = response.extract().path("refreshToken");

        assertEquals("Статус ответа авторизации не соответствует требуемому", SC_OK, statusCode);
        assertTrue("Пользователь не авторизован", isSuccess);

        userClient.logoutUser("{\"token\":\"" + refreshToken + "\"}");
    }

    @Test
    @DisplayName("Авторизация с неверным логином")
    public void loginUserByWrongLoginShowsFalse() {

        user.setEmail("a_" + user.getEmail());
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));

        int statusCode = loginResponse.extract().statusCode();
        boolean isSuccess = loginResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_UNAUTHORIZED, statusCode);
        assertFalse("Пользователь не должен быть авторизован", isSuccess);
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void loginUserByWrongPasswordShowsFalse() {

        user.setPassword("a_" + user.getPassword());
        ValidatableResponse loginResponse = userClient.loginUser(UserCredentials.from(user));

        int statusCode = loginResponse.extract().statusCode();
        boolean isSuccess = loginResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_UNAUTHORIZED, statusCode);
        assertFalse("Пользователь не должен быть авторизован", isSuccess);
    }
}
