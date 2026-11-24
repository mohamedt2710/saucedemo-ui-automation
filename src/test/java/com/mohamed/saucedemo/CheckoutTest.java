package com.mohamed.saucedemo;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the checkout flow: form validation, price totals, finishing an order, and cancelling.
 */
public class CheckoutTest extends BaseTest {

    /**
     * Shared helper: puts one product in the cart and walks to the checkout form.
     * Every checkout test starts from this point.
     */
    private void goToCheckoutForm() {
        login();
        addFirstProductToCart();
        driver.findElement(By.className("shopping_cart_link")).click();
        // Wait for the cart page to load, then click Checkout
        clickWhenReady(By.id("checkout"));
        // Wait for the checkout form to load before the test touches it
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name")));
    }

    /**
     * Shared helper: fills the checkout form with valid data and continues to the overview page.
     */
    private void fillFormAndContinue() {
        driver.findElement(By.id("first-name")).sendKeys("Mohamed");
        driver.findElement(By.id("last-name")).sendKeys("Tantawy");
        driver.findElement(By.id("postal-code")).sendKeys("12511");
        clickWhenReady(By.id("continue"));
        // The overview page takes a moment to load
        wait.until(ExpectedConditions.urlContains("checkout-step-two"));
    }

    @Test
    public void completeCheckout() {
        goToCheckoutForm();
        fillFormAndContinue();

        // On the overview page, finish the order
        clickWhenReady(By.id("finish"));

        assertEquals("Thank you for your order!",
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("complete-header"))).getText(),
                "The order should end with a thank you message");
    }

    @Test
    public void checkoutRequiresFirstName() {
        goToCheckoutForm();

        // Leave the form empty and try to continue
        clickWhenReady(By.id("continue"));

        String error = driver.findElement(By.cssSelector("[data-test='error']")).getText();
        assertTrue(error.contains("First Name is required"),
                "Expected a required-field error, got: " + error);
    }

    @Test
    public void checkoutOverviewTotals() {
        goToCheckoutForm();
        fillFormAndContinue();

        // Read the item price and the summary numbers from the overview page
        double itemPrice = readMoney(By.className("inventory_item_price"));
        double subtotal = readMoney(By.className("summary_subtotal_label"));
        double tax = readMoney(By.className("summary_tax_label"));
        double total = readMoney(By.className("summary_total_label"));

        // The math has to add up: subtotal equals the item price, total equals subtotal + tax
        assertEquals(itemPrice, subtotal, 0.001, "Subtotal should equal the item price");
        assertEquals(subtotal + tax, total, 0.001, "Total should equal subtotal plus tax");
    }

    @Test
    public void cancelReturnsToCart() {
        goToCheckoutForm();

        // Cancel on the form page should take us back to the cart
        clickWhenReady(By.id("cancel"));
        assertTrue(driver.getCurrentUrl().contains("cart"),
                "Cancel on the checkout form should go back to the cart");
    }

    /**
     * Reads a price like "$29.99" from an element and returns it as a number.
     */
    private double readMoney(By locator) {
        String text = driver.findElement(locator).getText();
        // Keep only digits and the dot, drop "$", "Item total:" and so on
        String number = text.replaceAll("[^0-9.]", "");
        return Double.parseDouble(number);
    }
}
