package com.mohamed.saucedemo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class for all tests.
 * It opens Chrome before each test, logs in when needed, and closes Chrome after each test.
 * Every test class extends this so we do not repeat the setup code.
 */
public class BaseTest {

    // The site we are testing (a public demo store made for practice)
    protected static final String BASE_URL = "https://www.saucedemo.com";

    // Standard test user that the demo site provides
    protected static final String USERNAME = "standard_user";
    protected static final String PASSWORD = "secret_sauce";

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Run Chrome without opening a window when we pass -Dheadless=true (useful on servers/CI)
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            // These two flags are needed when running as a restricted user, like in Docker or CI
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
        }

        // If Chrome is not in the default location, we can point to it with -Dchrome.binary=/path/to/chrome
        String chromeBinary = System.getProperty("chrome.binary");
        if (chromeBinary != null && !chromeBinary.isEmpty()) {
            options.setBinary(chromeBinary);
        }

        // Selenium Manager downloads the matching ChromeDriver automatically
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // Wait up to 10 seconds for elements before failing
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get(BASE_URL);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Logs in with the standard test user.
     * Used by every test that needs to be past the login page.
     */
    protected void login() {
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.id("login-button")).click();
        // Make sure the login worked AND the products are rendered before the test continues
        wait.until(ExpectedConditions.urlContains("inventory"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_item")));
    }

    /**
     * Adds the first product on the inventory page to the cart.
     * Several tests start from "one product in the cart", so this saves repetition.
     */
    protected void addFirstProductToCart() {
        driver.findElement(By.cssSelector(".inventory_item button")).click();
    }

    /**
     * Clicks an element once it is ready.
     * Swag Labs is a React app: right after a page loads the element is visible but its
     * click handler attaches a moment later, so a click that comes too fast is lost.
     * This short pause keeps the tests stable.
     */
    protected void clickWhenReady(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
        driver.findElement(locator).click();
    }
}
