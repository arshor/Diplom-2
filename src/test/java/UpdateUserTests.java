import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class UpdateUserTests {

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
        accessToken = response.extract().path("accessToken");
        accessToken = accessToken.split(" ")[1];
        refreshToken = response.extract().path("refreshToken");
    }

    @After
    public void cleanUp() {
        if (response != null) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Обновление данных авторизованного пользователя")
    public void updateLoginUserShowsTrue() {

        userClient.loginUser(UserCredentials.from(user));

        String newEmail = "a_" + user.getEmail();
        String newName = "a_" + user.getName();
        String updateData = "{\"email\": \"" + newEmail + "\", \"name\": \"" + newName + "\"}";
        ValidatableResponse updateResponse = userClient.updateUser(updateData, accessToken);
        int statusCode = updateResponse.extract().statusCode();
        boolean isSuccess = updateResponse.extract().path("success");

        ValidatableResponse getResponse = userClient.getUserData(accessToken);
        String updateEmail = getResponse.extract().path("user.email");
        String updateName = getResponse.extract().path("user.name");

        assertEquals("Статус ответа авторизации не соответствует требуемому", SC_OK, statusCode);
        assertTrue("Данные пользователя не обновлены", isSuccess);
        assertEquals("Логин не обновлен", newEmail, updateEmail);
        assertEquals("Имя не обновлено", newName, updateName);

        userClient.logoutUser("{\"token\":\"" + refreshToken + "\"}");
    }

    @Test
    @DisplayName("Обновление данных не авторизованного пользователя")
    public void updateNotLoginUserShowsFalse() {

        String newEmail = "a_" + user.getEmail();
        String newName = "a_" + user.getName();
        String updateData = "{\"email\": \"" + newEmail + "\", \"name\": \"" + newName + "\"}";
        ValidatableResponse updateResponse = userClient.updateUser(updateData, "");
        int statusCode = updateResponse.extract().statusCode();
        boolean isSuccess = updateResponse.extract().path("success");

        ValidatableResponse getResponse = userClient.getUserData(accessToken);
        String updateEmail = getResponse.extract().path("user.email");
        String updateName = getResponse.extract().path("user.name");

        assertEquals("Статус ответа авторизации не соответствует требуемому", SC_UNAUTHORIZED, statusCode);
        assertFalse("Данные пользователя не должны быть обновлены", isSuccess);
        assertNotEquals("Логин не должен быть обновлен", newEmail, updateEmail);
        assertNotEquals("Имя не должно быть обновлено", newName, updateName);
    }
}
