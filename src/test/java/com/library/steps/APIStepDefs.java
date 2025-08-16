package com.library.steps;


import com.library.utility.LibraryAPI_Util;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;


public class APIStepDefs {

    RequestSpecification givenPart = given().log().all();
    Response response;
    ValidatableResponse thenPart;
    JsonPath jp;
    public static Map<String,Object> randomDataMap;

    public static String bookID;
    public static String userId;
    String token;

    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String role) {
        givenPart.header("x-library-token", LibraryAPI_Util.getToken(role));
    }
    @Given("Accept header is {string}")
    public void accept_header_is(String acceptHeader) {
        givenPart.accept(acceptHeader);
    }
    @When("I send GET request to {string} endpoint")
    public void i_send_get_request_to_endpoint(String endPoint) {
        response=givenPart.get(endPoint);
        jp = response.jsonPath();
        // response.prettyPrint();
    }
    @Then("status code should be {int}")
    public void status_code_should_be(Integer expectedStatusCode) {
        thenPart=response.then();
        thenPart.statusCode(expectedStatusCode);
    }
    @Then("Response Content type is {string}")
    public void response_content_type_is(String expectedContentType) {
        thenPart.contentType(expectedContentType);
    }
    @Then("Each {string} field should not be null")
    public void each_field_should_not_be_null(String path) {
        thenPart.body(path,everyItem(notNullValue()));
    }


    @Given("Path param {string} is {string}")
    public void path_param_is(String pathParam, String value) {
        givenPart.pathParam(pathParam,value);
    }
    @Then("{string} field should be same with path param")
    public void field_should_be_same_with_path_param(String key) {

        thenPart.body(key,is(jp.getString(key)));
    }
    @Then("following fields should not be null")
    public void following_fields_should_not_be_null(List<String> paths) {
        for(String path : paths){
            assertNotNull(jp.getString(path));
        }
    }

    @Given("Request Content Type header is {string}")
    public void request_content_type_header_is(String contentType) {

        givenPart.contentType(contentType);
    }

    @Given("I create a random {string} as request body")
    public void i_create_a_random_as_request_body(String dataType) {
        randomDataMap = LibraryAPI_Util.getRandomBookMap();
        switch (dataType){
            case "book":
                randomDataMap = LibraryAPI_Util.getRandomBookMap();
                break;
            case "user":
                randomDataMap = LibraryAPI_Util.getRandomUserMap();
                break;
            default:
                throw new RuntimeException("Wrong data type is provided");
        }
        givenPart.formParams(randomDataMap);
         System.out.println("randomDataMap = " + randomDataMap);
    }
    @When("I send POST request to {string} endpoint")
    public void i_send_post_request_to_endpoint(String endPoint) {
        response = givenPart.when().post(endPoint);
        response.prettyPrint();

    }
    @Then("the field value for {string} path should be equal to {string}")
    public void the_field_value_for_path_should_be_equal_to(String path, String message) {
        thenPart.body(path,is(message));
    }
    @Then("{string} field should not be null")
    public void field_should_not_be_null(String path) {
        thenPart.body(path,is(notNullValue()));
        jp=response.jsonPath();
        bookID = jp.getString("book_id");
        userId = jp.getString("user_id");

    }

    @Given("I logged Library api with credentials {string} and {string}")
    public void i_logged_library_api_with_credentials_and(String email, String password) {
        token = LibraryAPI_Util.getToken(email,password);

    }
    @Given("I send {string} information as request body")
    public void i_send_information_as_request_body(String string) {
       // givenPart.body("token", ObjectMapperType.valueOf(token));
        givenPart.formParam("token",token);

    }


}
