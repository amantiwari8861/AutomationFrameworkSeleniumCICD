package com.training.stepdefinations;

import org.testng.Assert;

import com.training.driver.DriverFactory;
import com.training.pages.InventoryPage;
import com.training.pages.LoginPage;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoginSteps {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;

    private final String baseUrl = "https://www.saucedemo.com/";

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {

        loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.open(baseUrl);
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsIn(String username, String password) {

        inventoryPage = loginPage.login(username, password);
    }

    @Then("the user should be redirected to the inventory page")
    public void theUserShouldBeRedirectedToInventoryPage() {

        Assert.assertTrue(
            inventoryPage.getCurrentUrl().contains("inventory.html"),
            "Inventory page URL not found"
        );
    }

    @And("the page title should be {string}")
    public void thePageTitleShouldBe(String expectedTitle) {

        Assert.assertEquals(
            inventoryPage.getPageTitle(),
            expectedTitle
        );
    }
}