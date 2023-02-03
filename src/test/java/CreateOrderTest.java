import io.restassured.response.ValidatableResponse;
import org.example.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;


public class CreateOrderTest {

    private User user;
    private UserClient UserClient;
    private IngredientClient ingredientClient;
    private Ingredient ingredient;
    private List<String> ingredientList;
    private OrderClient orderClient;
    private String accessToken;
    private static final String NEED_INGREDIENT_IDS_TEXT = "Ingredient ids must be provided";

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
    public void createOrderWithAuthReturnsSuccessTrueAndStatus200() {
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

        Boolean statusOrder = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int numberOrder = orderResponse.extract().path("order.number");

        assertTrue(statusOrder);
        assertFalse(burgerName.isEmpty());
        assertTrue(numberOrder > 0);
    }

    @Test
    public void createOrderWithoutAuthReturnsSuccessTrueAndStatus200() {
        ValidatableResponse getIngredientResponse = ingredientClient.get();
        int statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        String hashIngredient = getIngredientResponse.extract().path("data._id[0]");
        ingredientList.add(hashIngredient);
        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = orderClient.create(ingredient, "");
        statusCode = orderResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        Boolean statusOrder = orderResponse.extract().path("success");
        String burgerName = orderResponse.extract().path("name");
        int numberOrder = orderResponse.extract().path("order.number");

        assertTrue(statusOrder);
        assertFalse(burgerName.isEmpty());
        assertTrue(numberOrder > 0);
    }

    @Test
    public void createOrderWithoutIngredientsReturnsStatus400AndMessageWithMistake() {
        ValidatableResponse getIngredientResponse = ingredientClient.get();
        int statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = orderClient.create(ingredient, "");
        statusCode = orderResponse.extract().statusCode();
        assertEquals(SC_BAD_REQUEST, statusCode);

        Boolean statusOrder = orderResponse.extract().path("success");
        String message = orderResponse.extract().path("message");

        assertFalse(statusOrder);
        assertEquals(NEED_INGREDIENT_IDS_TEXT, message);
    }

    @Test
    public void createOrderWithInvalidHashOfIngredientReturnsStatus500() {
        ValidatableResponse getIngredientResponse = ingredientClient.get();
        int statusCode = getIngredientResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        String hashIngredient = getIngredientResponse.extract().path("data._id[0]") + "1";
        ingredientList.add(hashIngredient);
        ingredient = new Ingredient(ingredientList);

        ValidatableResponse orderResponse = orderClient.create(ingredient, "");
        statusCode = orderResponse.extract().statusCode();

        assertEquals(SC_INTERNAL_SERVER_ERROR, statusCode);
    }
}

