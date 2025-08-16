package com.library.utility;

import com.github.javafaker.Faker;
import io.restassured.http.ContentType;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LibraryAPI_Util {

    /**
     * Provide email and password manually
     * Return directly POST /login request to obtain token in the response body
     */
    public static String getToken(String email, String password) {

        //Content type should be "application/x-www-form-urlencoded" to send form data in a key-value format. Not json. It works similarly to how HTML forms submit data. When sending a POST request with this format, the data is encoded as key-value pairs in the request body.

        // In our case, the email and password are sent as part of the body. The server reads these values, verifies them, and returns an API key if the credentials are valid. Since the data is in the request payload, it is part of the body. Even though it is encoded in a specific format, it is still in the body, unlike headers or URL parameters.

        // If we wanna automate these steps with RestAssured, we should use .formParam() to send form data in "application/x-www-form-urlencoded" format. This method encodes the parameters as key-value pairs in the request body.

        // .formParam() is used when the server expects "application/x-www-form-urlencoded" as the content type. The data is sent in the request body, not as query parameters. Multiple .formParam() calls add multiple key-value pairs. It should always be used with .contentType("application/x-www-form-urlencoded").
        return given()
                //.contentType(ContentType.JSON) //404
                //.contentType("application/x-www-form-urlencoded") //OK or
                .contentType(ContentType.URLENC) //OK
                .formParam("email", email)
                .formParam("password", password).
                when()
                .post(ConfigurationReader.getProperty("library.baseUri") + "/login")
                .prettyPeek()
                .then().statusCode(200)
                .extract().jsonPath().getString("token");
    }
    /**
     * Always adding email and password is not a good way
     * Provide userType as parameter
     * Call email and password from config.properties with Configuration.Reader
     * Return token as String by calling getToken(email, password) method provided username from /token endpoint
     */
    public static String getToken(String userType) {

        String email = ConfigurationReader.getProperty(userType + "_username");
        String password = ConfigurationReader.getProperty(userType + "_password");

        return getToken(email, password);
    }


    public static Map<String, Object> getRandomBookMap() {

        Faker faker = new Faker();
        Map<String, Object> bookMap = new LinkedHashMap<>();
        String randomBookName = "SH "+faker.book().title() + faker.number().numberBetween(0, 1000);
        //to differentiate the name of book we can add our initials
        bookMap.put("name", randomBookName);
        bookMap.put("isbn", faker.code().isbn10());
        bookMap.put("year", String.valueOf(faker.number().numberBetween(1000, 2021)));
        bookMap.put("author", faker.book().author());
        bookMap.put("book_category_id", String.valueOf(faker.number().numberBetween(1, 20)));  // in library app valid category_id is 1-20
        bookMap.put("description", faker.chuckNorris().fact());

        return bookMap;
    }

    public static Map<String, Object> getRandomUserMap() {

        Faker faker = new Faker();
        Map<String, Object> userMap = new LinkedHashMap<>();
        String fullName = faker.name().fullName();
        String email = fullName.substring(0, fullName.indexOf(" ")) +faker.number().numberBetween(1,100) +"@library";
        System.out.println(email);
        userMap.put("full_name", fullName);
        userMap.put("email", email);
        userMap.put("password", "libraryUser");
        // 2 is librarian as role
        userMap.put("user_group_id", "2");
        userMap.put("status", "ACTIVE");
        userMap.put("start_date", "2023-03-11");
        userMap.put("end_date", "2024-03-11");
        userMap.put("address", faker.address().cityName());

        return userMap;
    }


}
