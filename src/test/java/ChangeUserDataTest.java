import api.model.User;
import io.restassured.response.ValidatableResponse;
import api.client.UserClient;
import api.util.UserCredentials;
import api.util.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class ChangeUserDataTest {

    private User user;
    private UserClient UserClient;
    private String accessToken;
    private static final String email = "update@mail.com";
    private static final String name = "Praktikum";
    private static final String USER_SHOULD_BE_AUTHORISED_TEXT = "You should be authorised";


    @Before
    public void setUp() {
        user = UserGenerator.getUser();
        UserClient = new UserClient();
    }

    @After
    public void cleanUp() {
        if ( accessToken != null) {
            UserClient.delete(accessToken);
        }
    }

    @Test
    //Можно обновить данные юзера авторизованным юзером
    public void updateUserDataWithAuth() {
        ValidatableResponse createResponse = UserClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ValidatableResponse loginResponse = UserClient.login(UserCredentials.from(user));
        statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        accessToken = loginResponse.extract().path("accessToken");

        User newUserData = new User(email, name);
        ValidatableResponse updateResponse = UserClient.update(newUserData, accessToken);
        statusCode = updateResponse.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        String updatedEmail = updateResponse.extract().path("user.email");
        String updatedName = updateResponse.extract().path("user.name");

        assertEquals(email, updatedEmail);
        assertEquals(name, updatedName);
    }

    @Test
    //Невозможно обновить данные юзера неавторизованным юзером
    public void updateUserDataImpossibleWithoutAuth() {
        ValidatableResponse createResponse = UserClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);

        ValidatableResponse loginResponse = UserClient.login(UserCredentials.from(user));
        statusCode = loginResponse.extract().statusCode();

        assertEquals(SC_OK, statusCode);
        accessToken = loginResponse.extract().path("accessToken");


        User newUserData = new User(email, name);

        ValidatableResponse updateResponse = UserClient.update(newUserData, "");
        statusCode = updateResponse.extract().statusCode();

        assertEquals(SC_UNAUTHORIZED, statusCode);

        String message = updateResponse.extract().path("message");

        assertEquals(USER_SHOULD_BE_AUTHORISED_TEXT, message);
    }
}
