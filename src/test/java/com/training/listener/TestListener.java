package com.training.listener;



import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.training.report.ExtentManager;
import com.training.utils.ScreenshotUtils;

public final class TestListener implements ITestListener {

    private static final Logger log =
            LoggerFactory.getLogger(TestListener.class);

    private static final ThreadLocal<ExtentTest> CURRENT_TEST =
            new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        log.info("Test suite started: {}", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);

        ExtentTest extentTest = ExtentManager.getInstance()
                .createTest(testName);

        CURRENT_TEST.set(extentTest);

        extentTest.log(Status.INFO, "Test execution started");

        log.info("STARTED: {}", testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);

        log.info("PASSED: {}", testName);
        CURRENT_TEST.get().pass("Test passed successfully");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);
        Throwable failure = result.getThrowable();

        log.error("FAILED: {}", testName, failure);

        ExtentTest test = CURRENT_TEST.get();

        if (test != null) {
            test.log(Status.FAIL, "Test failed");

            if (failure != null) {
                test.fail(failure);
            }

            try {
                String screenshotPath =
                        ScreenshotUtils.capture(testName);

                test.addScreenCaptureFromPath(
                        Path.of(screenshotPath)
                                .toAbsolutePath()
                                .toString());

                test.info("Screenshot: " + screenshotPath);

            } catch (Exception e) {
                log.error(
                        "Unable to capture or attach failure screenshot",
                        e);

                test.warning("Failure screenshot was unavailable");
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = getTestName(result);

        log.warn("SKIPPED: {}", testName);

        ExtentTest test = CURRENT_TEST.get();

        if (test != null) {
            Throwable cause = result.getThrowable();

            if (cause != null) {
                test.skip(cause);
            } else {
                test.skip("Test was skipped");
            }
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(
            ITestResult result) {

        log.warn("Partial failure: {}", getTestName(result));
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Test suite finished: {}", context.getName());
        ExtentManager.flush();
    }

    private String getTestName(ITestResult result) {
        return result.getTestClass().getName()
                + "." + result.getMethod().getMethodName();
    }

    public static ExtentTest getCurrentTest() {
        return CURRENT_TEST.get();
    }

    public static void clearCurrentTest() {
        CURRENT_TEST.remove();
    }
}
