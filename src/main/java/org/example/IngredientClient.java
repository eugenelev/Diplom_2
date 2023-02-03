package org.example;

import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientClient extends Client {

    private static final String GET_PATH = "/api/ingredients";

    public ValidatableResponse get() {
        return given()
                .spec(getSpec())
                .when()
                .get(GET_PATH)
                .then();
    }
}
