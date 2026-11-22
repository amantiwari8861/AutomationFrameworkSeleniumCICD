package com.training.utils;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.training.config.ConfigReader;

public class WaitUtils {

    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this(driver, ConfigReader.getInt("explicitWaitSeconds"));
    }

    public WaitUtils(WebDriver driver, int timeoutInSeconds) {
        this.wait = new WebDriverWait(
            driver,
            Duration.ofSeconds(timeoutInSeconds)
        );

        this.wait.pollingEvery(Duration.ofMillis(500));
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    // Wait until element is visible
    public WebElement waitForVisibility(By locator) {
        return wait.until(
            ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    // Wait until element is clickable
    public WebElement waitForClickable(By locator) {
        return wait.until(
            ExpectedConditions.elementToBeClickable(locator)
        );
    }

    // Wait until element is present in DOM
    public WebElement waitForPresence(By locator) {
        return wait.until(
            ExpectedConditions.presenceOfElementLocated(locator)
        );
    }

    // Wait until element becomes invisible or disappears
    public boolean waitForInvisibility(By locator) {
        return wait.until(
            ExpectedConditions.invisibilityOfElementLocated(locator)
        );
    }

    // Wait until all matching elements are present
    public List<WebElement> waitForAllElements(By locator) {
        return wait.until(
            ExpectedConditions.presenceOfAllElementsLocatedBy(locator)
        );
    }

    // Wait until element contains expected text
    public boolean waitForText(By locator, String text) {
        return wait.until(
            ExpectedConditions.textToBePresentInElementLocated(
                locator, text
            )
        );
    }

    // Wait until page title contains expected text
    public boolean waitForTitle(String title) {
        return wait.until(
            ExpectedConditions.titleContains(title)
        );
    }

    // Wait until URL contains expected text
    public boolean waitForUrl(String url) {
        return wait.until(
            ExpectedConditions.urlContains(url)
        );
    }
}
