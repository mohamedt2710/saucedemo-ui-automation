package com.mohamed.saucedemo;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the login page: valid login, the three rejection cases, and logout.
 */
public class LoginTest extends BaseTest {

    @Test
    public void validLoginReachesInventory() {
        // Type the correct username and password, then click Login
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.id("login-button")).click();

        // A successful login lands on the products page
        assertTrue(driver.getCurrentUrl().contains("inventory"),
                "After login we should be on the inventory page");
        assertEquals("Products", driver.findElement(By.className("title")).getText());
    }

    @Test
    public void lockedOutUserIsRejected() {
        // locked_out_user is a special test account that is banned from the site
        driver.findElement(By.id("user-name")).sendKeys("locked_out_user");
        driver.findElement(By.id("password")).sendKeys(PASSWORD);
        driver.findElement(By.id("login-button")).click();

        // We stay on the login page and see the "locked out" message
        String error = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        assertTrue(error.contains("locked out"), "Expected the locked out message, got: " + error);
    }

    @Test
    public void wrongPasswordIsRejected() {
        driver.findElement(By.id("user-name")).sendKeys(USERNAME);
        driver.findElement(By.id("password")).sendKeys("wrong_password_123");
        driver.findElement(By.id("login-button")).click();

        String error = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        assertTrue(error.contains("do not match"), "Expected a credentials error, got: " + error);
    }

    @Test
    public void emptyFieldsAreRejected() {
        // Click Login without typing anything
        driver.findElement(By.id("login-button")).click();

        String error = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        assertTrue(error.contains("Username is required"), "Expected a required-field error, got: " + error);
    }

    @Test
    public void logoutReturnsToLoginPage() {
        login();

        // The logout link lives inside the side menu, which slides in with a short animation.
        // A normal click on the burger button is flaky in headless Chrome, so we click it with JavaScript -
        // a common workaround you will see in most Selenium courses.
        ((JavascriptExecutor) driver).executeScript("document.getElementById('react-burger-menu-btn').click()");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link")));
        // Same story as the burger button: a JavaScript click is the stable option here
        ((JavascriptExecutor) driver).executeScript("document.getElementById('logout_sidebar_link').click()");

        // After logout we are back on the login page with the login button visible
        assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button"))).isDisplayed(),
                "Login button should be visible after logout");
    }
}
