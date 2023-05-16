import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

public class CreateUserTests {

    private UserClient userClient;
    private User user;
    ValidatableResponse response;
    String accessToken;

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userClient = new UserClient();
        response = userClient.createUser(user);
    }

    @After
    public void cleanUp() {
        accessToken = response.extract().path("accessToken");
        response = userClient.deleteUser(accessToken.split(" ")[1]);
    }

    @Test
    @DisplayName("Создание нового пользователя успешно")
    public void createNewUserByGoodCredentialsShowsTrue() {

        int statusCode = response.extract().statusCode();
        boolean isSuccess = response.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_OK, statusCode);
        assertTrue("Новый пользователь не создан", isSuccess);
    }

    @Test
    @DisplayName("Создание нового пользователя с уже существующим логином")
    public void createNewUserWithTheSameLoginShowsFalse() {

        ValidatableResponse responseCreateNewUserWithTheSameLogin = userClient.createUser(user);;

        int statusCode = responseCreateNewUserWithTheSameLogin.extract().statusCode();
        boolean isSuccess = responseCreateNewUserWithTheSameLogin.extract().path("success");
        String message = responseCreateNewUserWithTheSameLogin.extract().path("message");

        assertEquals("Статус ответа не соответствует требуемому", SC_FORBIDDEN, statusCode);
        assertFalse("Новый пользователь не должен был создан", isSuccess);
        assertEquals("Не верное сообщение о том, что такой пользователь уже зарегистрирован",
                "User already exists", message);
    }

    @Test
    @DisplayName("Создание нового пользователя без логина")
    public void createNewUserWithoutLoginShowsFalse() {

        User newUser = UserGenerator.getRandom();
        newUser.setEmail(null);
        ValidatableResponse responseCreateNewUserWithoutLogin = userClient.createUser(newUser);;

        int statusCode = responseCreateNewUserWithoutLogin.extract().statusCode();
        boolean isSuccess = responseCreateNewUserWithoutLogin.extract().path("success");
        String message = responseCreateNewUserWithoutLogin.extract().path("message");

        assertEquals("Статус ответа не соответствует требуемому", SC_FORBIDDEN, statusCode);
        assertFalse("Новый пользователь не должен был создан", isSuccess);
        assertEquals("Не верное сообщение о том, что заполнены не все обязательные поля",
                "Email, password and name are required fields", message);
    }

    @Test
    @DisplayName("Создание нового пользователя без пароля")
    public void createNewUserWithoutPasswordShowsFalse() {

        User newUser = UserGenerator.getRandom();
        newUser.setPassword(null);
        ValidatableResponse responseCreateNewUserWithoutPassword = userClient.createUser(newUser);;

        int statusCode = responseCreateNewUserWithoutPassword.extract().statusCode();
        boolean isSuccess = responseCreateNewUserWithoutPassword.extract().path("success");
        String message = responseCreateNewUserWithoutPassword.extract().path("message");

        assertEquals("Статус ответа не соответствует требуемому", SC_FORBIDDEN, statusCode);
        assertFalse("Новый пользователь не должен был создан", isSuccess);
        assertEquals("Не верное сообщение о том, что заполнены не все обязательные поля",
                "Email, password and name are required fields", message);
    }

    @Test
    @DisplayName("Создание нового пользователя без имени")
    public void createNewUserWithoutNameShowsFalse() {

        User newUser = UserGenerator.getRandom();
        newUser.setName(null);
        ValidatableResponse responseCreateNewUserWithoutName = userClient.createUser(newUser);;

        int statusCode = responseCreateNewUserWithoutName.extract().statusCode();
        boolean isSuccess = responseCreateNewUserWithoutName.extract().path("success");
        String message = responseCreateNewUserWithoutName.extract().path("message");

        assertEquals("Статус ответа не соответствует требуемому", SC_FORBIDDEN, statusCode);
        assertFalse("Новый пользователь не должен был создан", isSuccess);
        assertEquals("Не верное сообщение о том, что заполнены не все обязательные поля",
                "Email, password and name are required fields", message);
    }
}
