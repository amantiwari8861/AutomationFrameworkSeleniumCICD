package com.training.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.training.listener.TestListener;

public class LoggingTest {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingTest.class);

    @Test(groups = {"smoke", "logging"})
    public void shouldTestAllLoggingMethods() {

        ExtentTest reportTest =
                TestListener.getCurrentTest();

        Assert.assertNotNull(
                reportTest,
                "ExtentTest must be initialized by TestListener");

        // TRACE: Detailed diagnostic information
        log.trace("TRACE: Entering logging test");
        reportTest.log(Status.INFO,
                "TRACE: Entering logging test");

        // DEBUG: Values useful during debugging
        String browser = "chrome";
        log.debug("DEBUG: Browser selected = {}", browser);
        reportTest.log(Status.INFO,
                "DEBUG: Browser selected = " + browser);

        // INFO: Normal execution milestones
        log.info("INFO: Framework initialization completed");
        reportTest.log(Status.INFO,
                "INFO: Framework initialization completed");

        // WARN: A recoverable or noteworthy condition
        log.warn("WARN: Example warning for verification");
        reportTest.log(Status.WARNING,
                "WARN: Example warning for verification");

        // ERROR: Error-level diagnostic example
        log.error("ERROR: Example error-level log for verification");
        reportTest.log(Status.INFO,
                "ERROR: Example error-level log for verification");

        Assert.assertTrue(true,
                "Logging methods executed successfully");
    }
}
