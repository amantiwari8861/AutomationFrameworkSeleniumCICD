package com.training.driver;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import com.training.config.ConfigReader;

public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static void createDriver() {
        if (DRIVER.get() != null) {
            throw new IllegalStateException(
                    "WebDriver already exists for this thread");
        }

        String browser = ConfigReader.get("browser")
                .toLowerCase();

        boolean headless = ConfigReader.getBoolean("headless");

        WebDriver webDriver;

        switch (browser) {
            case "chrome" -> {

                Map<String, Object> prefs = new HashMap<>();

                prefs.put("credentials_enable_service", false);
                prefs.put("profile.password_manager_enabled", false);
                prefs.put("profile.password_manager_leak_detection", false);

                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("prefs", prefs);
                if (headless) {
                    options.addArguments("--headless=new");
                }

                options.addArguments(
                        "--window-size=1920,1080",
                        "--disable-notifications"
                );

                webDriver = new ChromeDriver(options);
            }

            case "edge" -> {
                EdgeOptions options = new EdgeOptions();

                if (headless) {
                    options.addArguments("--headless=new");
                }

                options.addArguments(
                        "--window-size=1920,1080",
                        "--disable-notifications"
                );

                webDriver = new EdgeDriver(options);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported browser: " + browser);
        }

        try {
            webDriver.manage().timeouts()
                    .implicitlyWait(Duration.ZERO);

            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofSeconds(
                            ConfigReader.getInt(
                                    "pageLoadTimeoutSeconds")));

            DRIVER.set(webDriver);

        } catch (RuntimeException e) {
            webDriver.quit();
            throw e;
        }
    }

    public static WebDriver getDriver() {
        WebDriver webDriver = DRIVER.get();
        	
        if (webDriver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been initialized");
        }
        webDriver.manage().window().maximize();
        return webDriver;
    }

    public static void quitDriver() {
        WebDriver webDriver = DRIVER.get();

        try {
            if (webDriver != null) {
                webDriver.quit();
            }
        } finally {
            DRIVER.remove();
        }
    }
}
