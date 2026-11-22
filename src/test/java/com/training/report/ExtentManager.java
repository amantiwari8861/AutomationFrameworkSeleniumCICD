package com.training.report;



import java.nio.file.Files;
import java.nio.file.Path;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public final class ExtentManager {

    private static final ExtentReports EXTENT_REPORTS = createInstance();

    private ExtentManager() {
    }

    private static ExtentReports createInstance() {
        try {
            Path reportDirectory = Path.of("target", "reports");
            Files.createDirectories(reportDirectory);

            Path reportPath =
                    reportDirectory.resolve("extent-report.html");

            ExtentSparkReporter sparkReporter =
                    new ExtentSparkReporter(reportPath.toString());

            sparkReporter.config().setDocumentTitle(
                    "Selenium Automation Report");

            sparkReporter.config().setReportName(
                    "Test Execution Results");

            ExtentReports reports = new ExtentReports();
            reports.attachReporter(sparkReporter);

            reports.setSystemInfo("Framework", "Selenium + TestNG");
            reports.setSystemInfo("Logging", "SLF4J + Log4j 2");
            reports.setSystemInfo("Java Version",System.getProperty("java.version"));

            return reports;

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to initialize ExtentReports", e);
        }
    }

    public static ExtentReports getInstance() {
        return EXTENT_REPORTS;
    }

    public static void flush() {
        EXTENT_REPORTS.flush();
    }
}
