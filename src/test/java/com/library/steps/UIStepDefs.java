package com.library.steps;

import com.library.pages.BasePage;
import com.library.pages.BookPage;
import com.library.pages.LoginPage;
import com.library.utility.BrowserUtil;
import com.library.utility.DB_Util;
import com.library.utility.DatabaseHelper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.internal.common.assertion.Assertion;
import org.apache.commons.collections.ArrayStack;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.*;


public class UIStepDefs {

    LoginPage loginPage=new LoginPage();
    BasePage basePage = new BasePage() {
    };
    BookPage bookPage = new BookPage();
    Map<String, Object> userAsMap;

    @Given("I logged in Library UI as {string}")
    public void i_logged_in_library_ui_as(String userType) {

        loginPage.login(userType);
    }
    @Given("I navigate to {string} page")
    public void i_navigate_to_page(String moduleName) {
        bookPage.navigateModule(moduleName);
    }
    @Then("UI, Database and API created book information must match")
    public void ui_database_and_api_created_book_information_must_match() {

        String nameOfBook = (String)APIStepDefs.randomDataMap.get("name");
        String isbnOfBook = (String)APIStepDefs.randomDataMap.get("isbn");
        String yearOfBook = (String)APIStepDefs.randomDataMap.get("year");
        String authorOfBook = (String)APIStepDefs.randomDataMap.get("author");
        String idOfCategoryOfBook = (String)APIStepDefs.randomDataMap.get("book_category_id");
        String descriptionOfBook = (String)APIStepDefs.randomDataMap.get("description");

        //UI
        bookPage.search.sendKeys(nameOfBook+ Keys.ENTER);
        BrowserUtil.waitFor(2);

        List<String> newBook = new ArrayList<>();
        for (WebElement row : bookPage.allRows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (WebElement cell : cells) {
                newBook.add(cell.getText());
            }
        }
        assertEquals(nameOfBook,newBook.get(2));
        assertEquals(isbnOfBook,newBook.get(1));
        assertEquals(authorOfBook,newBook.get(3));
        assertEquals(yearOfBook,newBook.get(5));

        //DB
        System.out.println("APIStepDefs.bookID = " + APIStepDefs.bookID);
        DB_Util.runQuery("SELECT * from books\n" +
                "where id = "+APIStepDefs.bookID+";");
        List<String>dbNewBook=DB_Util.getRowDataAsList(1);
        System.out.println("dbNewBook = " + dbNewBook);
        Map<String, Object> bookAsMap = DB_Util.getRowMap(1);
        System.out.println("bookAsMap = " + bookAsMap);
        System.out.println("APIStepDefs.randomDataMap = " + APIStepDefs.randomDataMap);

        assertEquals(bookAsMap.get("name"),APIStepDefs.randomDataMap.get("name"));
        assertEquals(bookAsMap.get("isbn"),APIStepDefs.randomDataMap.get("isbn"));
        assertEquals(bookAsMap.get("year"),APIStepDefs.randomDataMap.get("year"));
        assertEquals(bookAsMap.get("author"),APIStepDefs.randomDataMap.get("author"));
        assertEquals(bookAsMap.get("book_category_id"),APIStepDefs.randomDataMap.get("book_category_id"));
        assertEquals(bookAsMap.get("description"),APIStepDefs.randomDataMap.get("description"));
    }

    @Then("created user information should match with Database")
    public void created_user_information_should_match_with_database() {

        System.out.println("APIStepDefs.randomDataMap = " + APIStepDefs.randomDataMap);
        System.out.println("APIStepDefs.userId = " + APIStepDefs.userId);
        DB_Util.runQuery("SELECT * from users\n" +
                "where id="+APIStepDefs.userId+";");

        userAsMap= DB_Util.getRowMap(1);
        System.out.println("bookAsMap = " + userAsMap);
        System.out.println("APIStepDefs.randomDataMap = " + APIStepDefs.randomDataMap);

        assertEquals(userAsMap.get("full_name"),APIStepDefs.randomDataMap.get("full_name"));
        assertEquals(userAsMap.get("email"),APIStepDefs.randomDataMap.get("email"));
        assertEquals(userAsMap.get("password"), DatabaseHelper.md5Hex(APIStepDefs.randomDataMap.get("password").toString()));
        assertEquals(userAsMap.get("user_group_id"),APIStepDefs.randomDataMap.get("user_group_id"));
        assertEquals(userAsMap.get("status"),APIStepDefs.randomDataMap.get("status"));
        assertEquals(userAsMap.get("start_date"),APIStepDefs.randomDataMap.get("start_date"));
        assertEquals(userAsMap.get("end_date"),APIStepDefs.randomDataMap.get("end_date"));
        assertEquals(userAsMap.get("address"),APIStepDefs.randomDataMap.get("address"));

    }
    @Then("created user should be able to login Library UI")
    public void created_user_should_be_able_to_login_library_ui() {

        String userName = APIStepDefs.randomDataMap.get("email").toString();
        String password = APIStepDefs.randomDataMap.get("password").toString();
        loginPage.login(userName,password);

    }
    @Then("created user name should appear in Dashboard Page")
    public void created_user_name_should_appear_in_dashboard_page() {

     assertEquals(userAsMap.get("full_name").toString(),basePage.accountHolderName.getText());



    }
}
