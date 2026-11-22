
package com.training.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.training.utils.WaitUtils;

public final class LoginPage {

    private final WebDriver driver;
    private final WaitUtils wait;

    private final By usernameInput = By.id("user-name");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.cssSelector("input[type='submit']");
    private final By errorContainerHeading = By.cssSelector("div.error-message-container.error h3");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
    }

    public LoginPage open(String baseUrl) {
        driver.get(baseUrl);
        wait.waitForVisibility(usernameInput);
        return this;
    }

    public LoginPage enterUsername(String username) {
        var element = wait.waitForVisibility(usernameInput);
        element.clear();
        element.sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        var element = wait.waitForVisibility(passwordInput);
        element.clear();
        element.sendKeys(password);
        return this;
    }

    public InventoryPage clickLogin() {
        wait.waitForClickable(loginButton).click();
        return !isErrorOccured() ? new InventoryPage(driver) : null;
    }

    public InventoryPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }
    public boolean isErrorOccured()
    {
    	try {
    		wait.waitForVisibility(errorContainerHeading).getText().equals("Epic sadface: Sorry, this user has been locked out.");
    		return true;
    	} catch (Exception e) {
			return false;
		}
    }
}
