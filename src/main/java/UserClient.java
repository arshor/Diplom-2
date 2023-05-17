import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient {

    private static final String REGISTER_PATH = "api/auth/register/";
    private static final String LOGIN_PATH = "api/auth/login/";
    private static final String LOGOUT_PATH = "api/auth/logout/";
    private static final String USER_PATH = "api/auth/user/";

    @Step("Регистрация нового пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(REGISTER_PATH)
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginUser(UserCredentials credentials) {
        return given()
                .spec(getBaseSpec())
                .body(credentials)
                .when()
                .post(LOGIN_PATH)
                .then();
    }

    @Step("Выход из системы пользователя")
    public ValidatableResponse logoutUser(String refreshToken) {
        return given()
                .spec(getBaseSpec())
                .body(refreshToken)
                .when()
                .post(LOGOUT_PATH)
                .then();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete(USER_PATH)
                .then();
    }

    @Step("Обновление данных пользователя")
    public ValidatableResponse updateUser(String updateData, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .body(updateData)
                .when()
                .patch(USER_PATH)
                .then();
    }

    @Step("Получение данных пользователя")
    public ValidatableResponse getUserData(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .get(USER_PATH)
                .then();
    }
}
