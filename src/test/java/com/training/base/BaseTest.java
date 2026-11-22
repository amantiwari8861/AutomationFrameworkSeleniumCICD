package com.training.base;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.training.config.ConfigReader;
import com.training.driver.DriverFactory;

public abstract class BaseTest {

    protected WebDriver driver;
    protected String baseUrl;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        baseUrl = ConfigReader.get("baseUrl");

        DriverFactory.createDriver();
        driver = DriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
        driver = null;
    }
}
