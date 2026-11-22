
package com.training.test;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.training.config.ConfigReader;

public class DeleteChats {

    private static final Duration TIMEOUT =
            Duration.ofSeconds(30);

    private static final By USERNAME = By.id("username");
    private static final By PASSWORD = By.id("password");

    private static final By SUBMIT_BUTTON =
            By.cssSelector("button[type='submit']");

    private static final By CHAT_NAVIGATION =
            By.cssSelector("a[class='ConsumerNavItem_link__Zj9e6']");

    private static final By CHAT_LIST =
            By.cssSelector(
                "div.ReactVirtualized__Grid__innerScrollContainer " +
                "div[role='listitem']"
            );

    private static final By CHAT_MENU =
            By.cssSelector(
                "button[id^='downshift-'][id$='-toggle-button']"
            );

    private static final By CLEAR_CHAT =
            By.cssSelector("a.pktQ3.SnUwI.hQMCs");

    @Test
    public void delete() {

        WebDriver driver = new ChromeDriver();

        WebDriverWait wait =
                new WebDriverWait(driver, TIMEOUT);

        try {

            driver.manage().window().maximize();

            driver.manage().timeouts()
                    .implicitlyWait(Duration.ZERO);

            driver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofSeconds(60));

            // STEP 1: Open login page
            driver.get("https://accounts.snapchat.com/v2/login");

            System.out.println("Login page: " + driver.getCurrentUrl());

            // STEP 2: Enter username
            type(wait, USERNAME,
                    ConfigReader.get("sc_username"));

            // STEP 3: Submit username
            click(wait, SUBMIT_BUTTON);

            // STEP 4: Enter password
            type(wait, PASSWORD,
                    ConfigReader.get("password"));

            // STEP 5: Submit password
            click(wait, SUBMIT_BUTTON);

            // STEP 6: Wait for Chat navigation
            WebElement chatNavigation = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            CHAT_NAVIGATION
                    )
            );

            // Capture existing windows before clicking
            Set<String> oldWindows =
                    new HashSet<>(driver.getWindowHandles());

            String oldWindow = driver.getWindowHandle();

            chatNavigation.click();

            // STEP 7: Handle same-tab OR new-tab navigation
            wait.until(d -> {

                Set<String> currentWindows =
                        d.getWindowHandles();

                if (currentWindows.size() > oldWindows.size()) {
                    return true;
                }

                // Same-tab navigation
                try {
                    return !d.getCurrentUrl().equals(
                            "https://accounts.snapchat.com/v2/login"
                    );
                } catch (NoSuchWindowException e) {
                    return false;
                }
            });

            // Switch only if a new tab actually opened
            Set<String> currentWindows =
                    driver.getWindowHandles();

            if (currentWindows.size() > oldWindows.size()) {

                currentWindows.removeAll(oldWindows);

                String newWindow = currentWindows.iterator().next();

                driver.switchTo().window(newWindow);

                System.out.println("Switched to new tab.");

            } else {

                System.out.println("Continuing in same tab.");
            }

            // STEP 8: Wait for Snapchat Web
            wait.until(
                    ExpectedConditions.urlContains(
                            "snapchat.com/web"
                    )
            );

            System.out.println(
                    "Chat URL: " + driver.getCurrentUrl()
            );

            // STEP 9: Wait for chat list
            wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            CHAT_LIST
                    )
            );

            System.out.println("Chat list loaded.");

            // STEP 10: Process chats
            int processed = 0;

            while (true) {

                List<WebElement> chats = wait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(
                                CHAT_LIST
                        )
                );

                // Preserve first chat
                if (chats.size() <= 1) {
                    break;
                }

                int previousCount = chats.size();

                // Always process second item because
                // deleting an item shifts the list
                WebElement chat = chats.get(1);

                wait.until(
                        ExpectedConditions.elementToBeClickable(chat)
                ).click();

                // Open chat options
                click(wait, CHAT_MENU);

                // Select clear chat
                click(wait, CLEAR_CHAT);

                // Wait for list update
                wait.until(d -> {

                    try {
                        return d.findElements(CHAT_LIST).size()
                                < previousCount;
                    } catch (StaleElementReferenceException e) {
                        return false;
                    }
                });

                processed++;

                System.out.println(
                        "Processed chats: " + processed
                );
            }

            System.out.println(
                    "Processing completed. Total: " + processed
            );

        } catch (Exception e) {

            e.printStackTrace();

            Assert.fail(
                    "Chat automation failed: " + e.getMessage(),
                    e
            );

        } finally {

            driver.quit();
        }
    }

    // Explicit wait for click
    private void click(WebDriverWait wait, By locator) {

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        element.click();
    }

    // Explicit wait for typing
    private void type(
            WebDriverWait wait,
            By locator,
            String value) {

        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );

        element.clear();
        element.sendKeys(value);
    }
}
