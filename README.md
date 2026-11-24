# Swag Labs UI Automation (Selenium + Java)

My first UI test automation project. It automates the same flows I covered by hand in my [manual testing portfolio](https://github.com/mohamedt2710/qa-manual-testing-portfolio), so I could practice turning manual test cases into code.

- **Site under test:** [Swag Labs](https://www.saucedemo.com) (a public demo store made for practice)
- **Tools:** Java, Selenium WebDriver, JUnit 5, Maven
- **Tests:** 15 tests covering login, the product list, the cart, and checkout

## Project layout

```
src/test/java/com/mohamed/saucedemo/
    BaseTest.java        - opens Chrome before each test, closes it after, has the shared login helper
    LoginTest.java       - 5 tests: valid login, 3 rejection cases, logout
    InventoryTest.java   - 4 tests: product list, sorting by price and by name, product details
    CartTest.java        - 2 tests: add to cart, remove from cart
    CheckoutTest.java    - 4 tests: full order, form validation, price totals, cancel
```

## How to run it

You need Java 11+ and Maven installed. Chrome must be installed; Selenium downloads the matching ChromeDriver by itself.

```bash
git clone https://github.com/mohamedt2710/saucedemo-ui-automation.git
cd saucedemo-ui-automation
mvn test
```

That is all three steps. The browser windows open, run the tests, and close on their own.

To run without opening browser windows (for example on a server):

```bash
mvn test -Dheadless=true
```

## What I learned

- Setting up WebDriver and why Selenium Manager removes the old chromedriver-download pain
- Finding elements with ids and CSS selectors
- Waiting properly with WebDriverWait instead of Thread.sleep
- Assertions with JUnit 5 and organizing tests so each one is independent
- Reading prices from the page and checking the checkout math in code
