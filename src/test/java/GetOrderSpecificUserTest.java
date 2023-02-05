import api.model.Ingredient;
import api.model.User;
import api.util.UserGenerator;
import api.util.UserCredentials;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import api.client.IngredientClient;
import api.client.OrderClient;
import api.client.UserClient;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetOrderSpecificUserTest {

    private User user;
    private UserClient UserClient;
    private IngredientClient ingredientClient;
    private Ingredient ingredient;
    private List<String> ingredientList;
    private OrderClient orderClient;
    private String accessToken;
    private static final String NEED_AUTH_TEXT = "You should be authorised";

    @Before
    public void setUp() {
        user = UserGenerator.getUser();
        UserClient = new UserClient();
        ingredientClient = new IngredientClient();
        orderClient = new OrderClient();
        ingredientList = new ArrayList<>();
    }

    @After
    public void cleanUp() {
        if ( accessToken != null) {
            UserClient.delete(accessToken);
        }
    }

    @Test
    public void getOrderNonAuthUserReturnStatus401AndMessageAboutAuth() {
        ValidatableResponse response = orderClient.get("");
        int statusCode = response.extract().statusCode();

        assertEquals(SC_UNAUTHORIZED, statusCode);

        String message = response.extract().path("message");

        assertEquals(NEED_AUTH_TEXT, message);
    }

    @Test
    public void getOrderAuthUserReturnStatus200AndListOrder() {
        UserClient.create(user);
        ValidatableResponse loginResponse = UserClient.login(UserCredentials.from(user));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        accessToken = loginResponse.extract().path("accessToken");

        ValidatableResponse getIngredientResponse = ingredientClient.get();
        statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        String hashIngredient = getIngredientResponse.extract().path("data._id[0]");
        ingredientList.add(hashIngredient);
        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = orderClient.create(ingredient, accessToken);
        statusCode = orderResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);


        ValidatableResponse userOrderResponse = orderClient.get(accessToken);
        statusCode = userOrderResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        int total = userOrderResponse.extract().path("total");
        int totalToday = userOrderResponse.extract().path("totalToday");
        List<Object> orders = userOrderResponse.extract().path("orders");

        assertTrue(orders.size() > 0);
        assertEquals(1, total);
        assertEquals(1, totalToday);
    }
}
