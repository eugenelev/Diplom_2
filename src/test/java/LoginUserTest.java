import io.restassured.response.ValidatableResponse;
import org.example.User;
import org.example.UserClient;
import org.example.UserCredentials;
import org.example.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.*;


public class LoginUserTest {

    private User user;

    private UserClient UserClient;

    private String accessToken;

    private static final String MESSAGE_INCORRECT_AUTH_DATA = "email or password are incorrect";

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
    //Успешная авторизация
    public void successfulLoginReturnsStatusCode200AndTokens() {
        UserClient.create(user);
        ValidatableResponse loginResponse = UserClient.login(UserCredentials.from(user));
        int statusCode = loginResponse.extract().statusCode();
        assertEquals(SC_OK, statusCode);


        accessToken = loginResponse.extract().path("accessToken");
        String refreshToken = loginResponse.extract().path("refreshToken");
        Boolean success = loginResponse.extract().path("success");


        assertFalse(accessToken.isEmpty());
        assertFalse(refreshToken.isEmpty());
        assertTrue(success);
    }

    @Test
    // Авторизация без логина невозможна
    public void AuthWithoutLoginReturnsStatusCode401AndMessageAboutInvalidData(){
        UserCredentials userWithoutLogin = new UserCredentials("", user.getPassword());
        ValidatableResponse loginResponse = UserClient.login(userWithoutLogin);
        int statusCode = loginResponse.extract().statusCode();

        assertEquals(SC_UNAUTHORIZED, statusCode);

        String message = loginResponse.extract().path("message");

        assertEquals(MESSAGE_INCORRECT_AUTH_DATA, message);
    }

}