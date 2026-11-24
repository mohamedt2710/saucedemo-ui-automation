package com.mohamed.saucedemo;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the inventory (products) page: listing, sorting, and the product detail page.
 */
public class InventoryTest extends BaseTest {

    @Test
    public void allProductsAreListed() {
        login();

        // The demo store always shows 6 products to the standard user
        List<WebElement> products = driver.findElements(By.className("inventory_item"));
        assertEquals(6, products.size(), "The store should list 6 products");
    }

    @Test
    public void sortByPriceLowToHigh() {
        login();

        // The sort control is a normal dropdown
        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        sortDropdown.selectByVisibleText("Price (low to high)");

        // Collect the prices in the order they are shown
        List<Double> prices = driver.findElements(By.className("inventory_item_price"))
                .stream()
                .map(e -> Double.parseDouble(e.getText().replace("$", "")))
                .collect(Collectors.toList());

        // Each price must be smaller than or equal to the next one
        for (int i = 0; i < prices.size() - 1; i++) {
            assertTrue(prices.get(i) <= prices.get(i + 1),
                    "Prices are not sorted low to high: " + prices);
        }
    }

    @Test
    public void sortByNameZtoA() {
        login();

        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        sortDropdown.selectByVisibleText("Name (Z to A)");

        List<String> names = driver.findElements(By.className("inventory_item_name"))
                .stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());

        // First item must come after (or equal) the second alphabetically, and so on
        for (int i = 0; i < names.size() - 1; i++) {
            assertTrue(names.get(i).compareTo(names.get(i + 1)) >= 0,
                    "Names are not sorted Z to A: " + names);
        }
    }

    @Test
    public void productDetailPage() {
        login();

        // Click the first product name, then check the detail page shows its info
        String nameOnList = driver.findElement(By.className("inventory_item_name")).getText();
        driver.findElement(By.className("inventory_item_name")).click();

        // Wait for the detail page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("inventory_details_name")));

        assertTrue(driver.getCurrentUrl().contains("inventory-item"),
                "Clicking a product should open its detail page");
        assertEquals(nameOnList,
                driver.findElement(By.className("inventory_details_name")).getText(),
                "The detail page should show the same product we clicked");
    }
}
