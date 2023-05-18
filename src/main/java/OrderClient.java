import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {

    private List<String> ingredientsList;
    private static final String GET_INGREDIENTS = "api/ingredients/";
    private static final String CREATE_GET_ORDER = "api/orders/";

    @Step("Получение списка ингредиентов")
    public List<String> getIngredients() {
        return given()
                .spec(getBaseSpec())
                .get(GET_INGREDIENTS)
                .then()
                .extract()
                .path("data._id");
    }

    @Step("Создание нового заказа")
    public ValidatableResponse createOrder(Order order, String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post(CREATE_GET_ORDER)
                .then();
    }

    @Step("Получение списка заказов")
    public ValidatableResponse getOrders(String accessToken) {
        return given()
                .spec(getBaseSpec())
                .header("Authorization", accessToken)
                .when()
                .get(CREATE_GET_ORDER)
                .then();
    }
}
