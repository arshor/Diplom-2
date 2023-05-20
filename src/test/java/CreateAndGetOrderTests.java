import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

public class CreateAndGetOrderTests {

    private UserClient userClient;
    private User user;
    private OrderClient orderClient;
    private ValidatableResponse response;
    private String accessToken;

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userClient = new UserClient();
        response = userClient.createUser(user);
        accessToken = response.extract().path("accessToken");

        orderClient = new OrderClient();
    }

    @After
    public void cleanUp() {
        if (response.extract().statusCode() == SC_OK) {
            userClient.deleteUser(accessToken.split(" ")[1]);
        }
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем с ингредиентами")
    public void createOrderByLoginUserWithIngredientsShowsTrue() {

        ValidatableResponse createOrderResponse = orderClient.createOrder(createIngredientsList(), accessToken);
        int statusCode = createOrderResponse.extract().statusCode();
        boolean isSuccess = createOrderResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_OK, statusCode);
        assertTrue("Заказ не создан", isSuccess);
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем без ингредиентов")
    public void createOrderByLoginUserWithoutIngredientsShowsFalse() {

        ValidatableResponse createOrderResponse = orderClient.createOrder(new Order(), accessToken);
        int statusCode = createOrderResponse.extract().statusCode();
        boolean isSuccess = createOrderResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_BAD_REQUEST, statusCode);
        assertFalse("Заказ не должен быть создан", isSuccess);
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем с ингредиентами")
    public void createOrderByNotLoginUserWithIngredientsShowsTrue() {

        ValidatableResponse createOrderResponse = orderClient.createOrder(createIngredientsList(), "");
        int statusCode = createOrderResponse.extract().statusCode();
        boolean isSuccess = createOrderResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_OK, statusCode);
        assertTrue("Заказ не создан", isSuccess);
    }

    @Test
    @DisplayName("Создание заказа неавторизованным пользователем без ингредиентов")
    public void createOrderByNotLoginUserWithoutIngredientsShowsFalse() {

        ValidatableResponse createOrderResponse = orderClient.createOrder(new Order(), "");
        int statusCode = createOrderResponse.extract().statusCode();
        boolean isSuccess = createOrderResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_BAD_REQUEST, statusCode);
        assertFalse("Заказ не должен быть создан", isSuccess);
    }

    @Test
    @DisplayName("Создание заказа авторизованным пользователем с неверным хешем ингредиентов")
    public void createOrderByLoginUserWithWrongHashIngredientsShowsFalse() {

        Order orderWithWrongHash = new Order();
        orderWithWrongHash.setIngredients(List.of("qwerty12345678"));

        ValidatableResponse createOrderResponse = orderClient.createOrder(orderWithWrongHash, accessToken);
        int statusCode = createOrderResponse.extract().statusCode();

        assertEquals("Статус ответа не соответствует требуемому", SC_INTERNAL_SERVER_ERROR, statusCode);
    }

    @Test
    @DisplayName("Получение списка заказов авторизованным пользователем")
    public void getOrderListByLoginUserShowsTrue() {

        orderClient.createOrder(createIngredientsList(), accessToken);

        ValidatableResponse getOrderListResponse = orderClient.getOrders(accessToken);
        int statusCode = getOrderListResponse.extract().statusCode();
        boolean isSuccess = getOrderListResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_OK, statusCode);
        assertTrue("Список заказов не получен", isSuccess);
    }

    @Test
    @DisplayName("Получение списка заказов не авторизованным пользователем")
    public void getOrderListByNotLoginUserShowsFalse() {

        orderClient.createOrder(createIngredientsList(), accessToken);

        ValidatableResponse getOrderListResponse = orderClient.getOrders("");
        int statusCode = getOrderListResponse.extract().statusCode();
        boolean isSuccess = getOrderListResponse.extract().path("success");

        assertEquals("Статус ответа не соответствует требуемому", SC_UNAUTHORIZED, statusCode);
        assertFalse("Список заказов не должен быть получен", isSuccess);
    }

    @DisplayName("Создание списка ингредиентов")
    public Order createIngredientsList() {

        List<String> ingredientsFullList = orderClient.getIngredients();

        List<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(ingredientsFullList.get(0));
        ingredientsList.add(ingredientsFullList.get(1));

        return new Order(ingredientsList);
    }
}
