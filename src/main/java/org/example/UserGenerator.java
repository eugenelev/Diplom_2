package org.example;
import com.github.javafaker.Faker;

public class UserGenerator {

    public static Faker faker = new Faker();


    private static String email = faker.internet().emailAddress();
    private static String password = faker.internet().password();
    private static String name = faker.name().name();

    public static User getUser() {
        return new User(email, password, name);
    }


    public static User getCourierWithoutEmail() {
        return new User("", password, name);
    }
}
