import api.model.User;
import io.restassured.response.ValidatableResponse;
import api.client.UserClient;
import api.util.UserCredentials;
import api.util.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;


public class CreateUserTest {

    private User user;
    private UserClient UserClient;
    private String token;
    private static final String USER_EXISTS_TEXT = "User already exists";
    private static final String MESSAGE_NOT_ENOUGH_DATA = "Email, password and name are required fields";


    @Before
    public void setUp() {
        user = UserGenerator.getUser();
        UserClient = new UserClient();
    }

    @After
    public void cleanUp() {
        if ( token != null) {
            UserClient.delete(token);
        }
    }

    @Test
    //Можно создать юзера
    public void CreateNewUser() {
        ValidatableResponse response = UserClient.create(user);
        int statusCode = response.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        ValidatableResponse loginResponse = UserClient.login(UserCredentials.from(user));
        statusCode = loginResponse.extract().statusCode();

        assertEquals(SC_OK, statusCode);

        token = loginResponse.extract().path("accessToken");
    }

    @Test
    //Невозможно создать уже существующего юзера
    public void MustNotCreateNewUserWithExistingData(){
        UserClient.create(user);
        ValidatableResponse response = UserClient.create(user);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_FORBIDDEN, statusCode);

        String message = response.extract().path("message");
        assertEquals(message, USER_EXISTS_TEXT);
    }

    @Test
    //Невозможно создать юзера без почты
    public void CreateNewCourierWithoutLogin(){
        User courierWithoutEmail = UserGenerator.getCourierWithoutEmail();
        ValidatableResponse response = UserClient.create(courierWithoutEmail);

        int statusCode = response.extract().statusCode();
        assertEquals(SC_FORBIDDEN, statusCode);


        String message = response.extract().path("message");
        assertEquals(message, MESSAGE_NOT_ENOUGH_DATA);

    }
}