package com.library.steps;


import com.library.utility.ConfigurationReader;
import com.library.utility.DB_Util;
import com.library.utility.Driver;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.time.Duration;

public class Hooks {

    @Before()
    public void setBaseURI(){
        RestAssured.baseURI=ConfigurationReader.getProperty("library.baseUri");
    }


    //a little bit different after structure then until you see:
    //this method executes after each test and give us meaningful messages. How?
    //gives us the name of scenario and status of them: passed or failed, it will be helpful all the test passed but just one scenario failed you will identify
    @After()
    public void endScenario(Scenario scenario){
        System.out.println("Test Result for \""+scenario.getName()+"\" --> "+scenario.getStatus());
    }

    @Before("@ui")
    public void setUp(){

        Driver.getDriver().manage().window().maximize();
        Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        Driver.getDriver().get(ConfigurationReader.getProperty("library_url"));

    }

    @After("@ui")
    public void tearDown(Scenario scenario){
        if(scenario.isFailed()){
            final byte[] screenshot = ((TakesScreenshot) Driver.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot,"image/png","screenshot");
        }

        Driver.closeDriver();

    }

    @Before("@db")
    public void setUpDB(){
        DB_Util.createConnection();
    }

    @After("@db")
    public void tearDownDB(){
        DB_Util.destroy();
    }

}

