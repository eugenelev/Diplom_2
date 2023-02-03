package org.example;
import io.restassured.response.ValidatableResponse;
import static io.restassured.RestAssured.given;

public class OrderClient extends Client {

    private static final String PATH = "/api/orders";



    public ValidatableResponse create(Ingredient ingredient, String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(ingredient)
                .when()
                .post(PATH)
                .then();

    }

    public ValidatableResponse get(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .get(PATH)
                .then();
    }
}
