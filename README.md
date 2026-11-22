# Selenium Automation Framework

A small UI automation framework built on **Java 21**, **Selenium 4**, **TestNG**, **ExtentReports**, and **Log4j 2**, with a Page Object Model and per-test reporting/screenshots.

> **Status: training scaffold.** The structure is sound, but see [Known limitations](#known-limitations) before using this as a project baseline. The sample login test does not pass against the shipped configuration.

---

## Tech stack

| Component | Version / role |
| --- | --- |
| Java | 21 (`maven.compiler.release=21`) |
| Selenium | 4.50.0 (Chrome / Edge via Selenium Manager) |
| TestNG | 7.12.0 |
| ExtentReports | 5.1.2 (HTML report) |
| Log4j 2 | 2.26.1 + `log4j-slf4j2-impl` |
| Build | Maven (no wrapper committed) |
| IDE | Eclipse / m2e metadata included (`.project`, `.classpath`, `.settings/`) |

---

## Prerequisites

- **JDK 21** on `PATH` (`java -version`)
- **Maven 3.9+** on `PATH` (`mvn -version`)
- **Google Chrome** and/or **Microsoft Edge** installed
  - Drivers are resolved automatically by Selenium Manager; no manual `chromedriver` download needed.

---

## Quick start

1. **Create your local configuration** (ignored by git):

   ```powershell
   Copy-Item config.properties.example src/main/resources/config.properties
   ```

   The file is intentionally not committed. `ConfigReader` fails fast with
   `config.properties was not found` if it is missing.

2. **Run the tests:**

   ```powershell
   mvn clean test
   ```

3. **Open the report:**

   ```powershell
   Start-Process target/reports/extent-report.html
   ```

---

## Configuration

`src/main/resources/config.properties` (see `config.properties.example`):

| Key | Description | Example |
| --- | --- | --- |
| `baseUrl` | Application under test | `https://www.saucedemo.com/` |
| `browser` | `chrome` or `edge` | `chrome` |
| `headless` | Run without a visible window | `false` |
| `explicitWaitSeconds` | Wait used by page objects | `10` |
| `pageLoadTimeoutSeconds` | Max page load time | `30` |
| `sc_username` | Snapchat user, **only** for `DeleteChats` | *(blank)* |
| `password` | Snapchat password, **only** for `DeleteChats` | *(blank)* |

### Override precedence

`ConfigReader.get(key)` resolves in this order:

1. JVM system property — `-DbaseUrl=https://example.com`
2. Environment variable with the **exact key name** — e.g. `baseUrl`
3. `config.properties`

This means CI can inject values without editing files:

```powershell
mvn test -Dbrowser=edge -Dheadless=true
```

---

## Project layout

```text
pom.xml                          Maven build and dependencies
testng.xml                       TestNG suite definition (groups + classes)
config.properties.example        Template for local configuration
src/main/java/com/training/
  config/ConfigReader.java       Property resolution (sysprop > env > file)
  driver/DriverFactory.java      ThreadLocal WebDriver, Chrome/Edge, headless
  utils/ScreenshotUtils.java     Timestamped PNG capture -> target/screenshots
src/main/resources/
  config.properties              Local only (git-ignored)
  log4j2.xml                     Console + rolling file logging
src/test/java/com/training/
  base/BaseTest.java             Driver setup/teardown per test method
  listener/TestListener.java     Extent reporting + screenshot on failure
  report/ExtentManager.java      ExtentReports singleton
  pages/LoginPage.java           Login page object
  test/LoginTest.java            Login smoke/regression cases
  test/LoggingTest.java          Verifies all log levels and report entries
  test/DeleteChats.java          Standalone Snapchat cleanup script (not in suite)
target/                          Build output, logs, reports, screenshots
test-output/                     Native TestNG reporters (generated)
```

---

## Running tests

| Goal | Command |
| --- | --- |
| Full build + tests | `mvn clean test` |
| One class | `mvn test -Dtest=LoginTest` |
| One method | `mvn test -Dtest=LoginTest#validLoginShouldOpenSecurePage` |
| Headless | `mvn test -Dheadless=true` |
| Edge instead of Chrome | `mvn test -Dbrowser=edge` |
| A group (Surefire/TestNG) | `mvn test -Dgroups=smoke` |

> **Important:** `testng.xml` is **not** wired into Maven yet. `mvn test` uses Surefire's default class discovery (`**/*Test.java`), so it will run `LoginTest` and `LoggingTest` but ignore the group filters and skip `DeleteChats`. Runs from Eclipse/TestNG may use `testng.xml` instead, which means IDE and CLI results can differ.

---

## Reports, logs and screenshots

| Output | Location |
| --- | --- |
| Extent HTML report | `target/reports/extent-report.html` |
| Failure screenshots | `target/screenshots/*.png` |
| Application log | `target/logs/automation.log` (+ rotated `.log.gz`) |
| Native TestNG report | `test-output/index.html` |

All of the above are git-ignored.

---

## Writing a new test

1. Add a page object under `src/test/java/com/training/pages/` (locators + actions, no assertions).
2. Extend `BaseTest` so the driver is created and quit automatically:

   ```java
   package com.training.test;

   import org.testng.Assert;
   import org.testng.annotations.Test;

   import com.training.base.BaseTest;
   import com.training.pages.LoginPage;

   public class ExampleTest extends BaseTest {

       @Test(groups = {"smoke"})
       public void userCanLogIn() {
           LoginPage page = new LoginPage(driver);
           page.open(baseUrl);
           page.login("standard_user", "secret_sauce");
           Assert.assertTrue(driver.getCurrentUrl().contains("/inventory"));
       }
   }
   ```

3. Use `explicitWaitSeconds` via `LoginPage`-style waits; do **not** add implicit waits (`DriverFactory` deliberately sets implicit wait to `0`).
4. Register the class in `testng.xml` if you intend to run through the suite.

`TestListener` creates the `ExtentTest` and captures failure screenshots automatically, so tests should not build report nodes themselves. `LoggingTest` is the exception: it reads `TestListener.getCurrentTest()` to assert the listener ran.

---

## Test groups

| Group | Purpose | Included in `testng.xml`? |
| --- | --- | --- |
| `smoke` | Fast happy-path checks | Yes |
| `logging` | Logging/reporting verification | Yes |
| `regression` | Broader checks | **No** |

`LoginTest.invalidLoginShouldDisplayError` is tagged `regression`, so the suite never runs it.

---

## Known limitations

- **Login test target mismatch** — `baseUrl` points at SauceDemo, but `LoginPage` and `LoginTest` use the-internet locators (`#username`, `/secure`, `tomsmith`). The test fails in `LoginPage.open()`. Align the page object with the configured site (SauceDemo uses `#user-name`, `#password`, `#login-button`, `/inventory.html`).
- **Suite not wired to Maven** — see the note under [Running tests](#running-tests).
- **`DeleteChats` is standalone and destructive** — it manages its own `ChromeDriver`, targets a real Snapchat account, uses obfuscated CSS classes, and is not part of `testng.xml`. Do not enable it accidentally.
- **No parallel execution configuration** — `DriverFactory` is `ThreadLocal`-ready, but TestNG parallelism is not configured.
- **Single static Extent report** — `ExtentManager` initializes once per JVM; multi-suite runs need a flush-at-suite-end strategy.
- **Screenshot capture needs an active driver** — failures in non-UI tests (e.g. `LoggingTest`) log a warning instead of attaching an image.

---

## Security

- `src/main/resources/config.properties` is **git-ignored**. Only `config.properties.example` should be committed.
- Prefer `-Dkey=value` or environment variables for credentials, especially in CI.
- If a credential was ever committed, shared, or packaged, **rotate it** — adding it to `.gitignore` does not remove it from history or from existing build artifacts.
- Run `git status --ignored` after `git init` to confirm `target/`, `test-output/`, and the real config file are excluded.
