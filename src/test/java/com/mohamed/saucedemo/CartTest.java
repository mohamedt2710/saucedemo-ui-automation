package com.mohamed.saucedemo;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the shopping cart: adding and removing products.
 */
public class CartTest extends BaseTest {

    @Test
    public void addProductsToCart() {
        login();

        // Remember the name of the first product, then add it
        String productName = driver.findElement(By.className("inventory_item_name")).getText();
        addFirstProductToCart();

        // The cart badge takes a moment to appear, so wait for it
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("shopping_cart_badge")));

        // The cart badge on the top right should show 1
        assertEquals("1", driver.findElement(By.className("shopping_cart_badge")).getText());

        // Open the cart and check the same product is inside
        driver.findElement(By.className("shopping_cart_link")).click();
        // Re-read the name from the cart page (the page re-renders, so we locate it fresh)
        assertEquals(productName,
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_item_name"))).getText(),
                "The cart should contain the product we added");
    }

    @Test
    public void removeProductFromCart() {
        login();
        addFirstProductToCart();

        // Go to the cart and remove the product
        driver.findElement(By.className("shopping_cart_link")).click();
        clickWhenReady(By.cssSelector(".cart_item button"));

        // The cart should be empty again (no items and no badge)
        assertEquals(0, driver.findElements(By.className("cart_item")).size(),
                "Cart should be empty after removing the product");
        assertTrue(driver.findElements(By.className("shopping_cart_badge")).isEmpty(),
                "The cart badge should disappear when the cart is empty");
    }
}
