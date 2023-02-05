package api.client;
import api.model.Ingredient;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client {

    private static final String PATH = "/api/orders";

    @Step("Создание заказа")
    public ValidatableResponse create(Ingredient ingredient, String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(ingredient)
                .when()
                .post(PATH)
                .then();

    }
    @Step("Получение списка заказов")
    public ValidatableResponse get(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .get(PATH)
                .then();
    }
}
