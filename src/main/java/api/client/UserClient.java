package api.client;
import api.model.User;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import api.util.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {

    private static final String CREATE_PATH = "/api/auth/register";

    private static final String LOGIN_PATH = "/api/auth/login";

    private static final String DELETE_PATH = "/api/auth/user";

    private static final String UPDATE_PATH = "/api/auth/user";

    @Step("Создание пользователя")
    public ValidatableResponse create (User user) {
        return given()
                .spec(getSpec())
                .body(user)
                .when()
                .post(CREATE_PATH)
                .then();

    }

    @Step("Авторизация юзера")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getSpec())
                .body(userCredentials)
                .when()
                .post(LOGIN_PATH)
                .then();

    }

    @Step("Удаление юзера")
    public ValidatableResponse delete(String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .when()
                .delete(DELETE_PATH)
                .then();

    }

    @Step("Обновление данных юзера")
    public ValidatableResponse update(User user, String token) {
        return given()
                .spec(getSpec())
                .header("Authorization", token)
                .body(user)
                .when()
                .patch(UPDATE_PATH)
                .then();
    }
}
