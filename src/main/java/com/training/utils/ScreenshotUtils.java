package com.training.utils;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.training.driver.DriverFactory;


public final class ScreenshotUtils {

    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");

    private ScreenshotUtils() {
    }

    public static String capture(String testName) {
        WebDriver driver = DriverFactory.getDriver();

        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            throw new IllegalStateException(
                    "Browser does not support screenshots");
        }

        String safeName = testName.replaceAll(
                "[^a-zA-Z0-9._-]", "_");

        String timestamp = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(
                        "yyyyMMdd_HHmmss_SSS"));

        Path destination = SCREENSHOT_DIR.resolve(
                safeName + "_" + timestamp + ".png");

        try {
            Files.createDirectories(SCREENSHOT_DIR);

            Path source = screenshotDriver.getScreenshotAs(OutputType.FILE).toPath();

            Files.copy(
                    source,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING);

            return destination.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to save screenshot", e);
        }
    }
}



