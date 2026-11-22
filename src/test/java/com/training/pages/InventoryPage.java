package com.training.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.training.utils.WaitUtils;

public class InventoryPage {
	private final WaitUtils wait;
    private final WebDriver driver;

    private final By hamburgerIcon = By.id("react-burger-menu-btn");
    private final By hamburgerCrossIcon = By.id("react-burger-cross-btn");
    private final By cartIcon = By.className("shopping_cart_link");
    private final By inventoryList = By.className("inventory_list");
    private final By filterDropdown = By.className("product_sort_container");
    private final By title = By.cssSelector("span[class='title']");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        wait.waitForVisibility(title);
    }

    public void openSidebar() {
    	System.out.println("opening sidebar!");
        wait.waitForClickable(hamburgerIcon).click();
        System.out.println("Sidebar Opened!");
    }
    public void closeSidebar() {
        wait.waitForClickable(hamburgerCrossIcon).click();
    }
    
    public String getPageTitle()
    {
    	return wait.waitForVisibility(title).getText();
    }
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
    
    public InventoryPage testActions()
    {
    	Actions mouse=new Actions(driver);
    	WebElement firstProductName = driver.findElement(By.className("inventory_item_name"));
    	mouse.moveToElement(firstProductName).perform();
    	// H.W : https://automation-testing-website-chi.vercel.app/
    	JavascriptExecutor js=(JavascriptExecutor)driver;
//    	js.executeScript("alert('Good Afternoon Everyone');");
    	js.executeScript("""
    			arguments[0].style.backgroundColor='red';
    			arguments[0].style.color='yellow'
    			""",firstProductName);
    	return this;
    }
}
