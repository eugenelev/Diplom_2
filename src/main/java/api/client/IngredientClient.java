package api.client;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientClient extends Client {

    private static final String GET_PATH = "/api/ingredients";

    @Step("Получение списка ингредиентов")
    public ValidatableResponse get() {
        return given()
                .spec(getSpec())
                .when()
                .get(GET_PATH)
                .then();
    }
}
