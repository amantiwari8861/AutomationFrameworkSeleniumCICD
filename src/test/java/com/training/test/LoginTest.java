package com.training.test;

import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.training.base.BaseTest;
import com.training.pages.InventoryPage;
import com.training.pages.LoginPage;
import com.training.utils.ExcelUtils;

public class LoginTest extends BaseTest {

	@Test
	public void readExcelFile() {
		try {
			Object[][] sheetData = ExcelUtils.readSheet("CorrectUser.xlsx", "Sheet1", true);
			for (Object[] objects : sheetData) {
				for (Object object : objects) {
					System.out.print("\t" + (String) object);
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@DataProvider
	public Object[][] correctUsersDataProvider() {
		Object[][] credentials=null;
		try {
			Object[][] sheetData = ExcelUtils.readSheet("CorrectUser.xlsx", "Sheet1", true);
			credentials=new Object[sheetData.length][];
			for (int row = 0; row < sheetData.length; row++) 
			{
				credentials[row]=new Object[sheetData.length-3];
				for (int col = 0; col < sheetData[row].length; col++) 
				{
					if(col==0)
						continue;
					credentials[row][col-1]=sheetData[row][col];
					System.out.print("\t" + (String) credentials[row][col-1]);
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return credentials;
	}

	@Test(dataProvider = "correctUsersDataProvider")
	public void testCorrectUserLogin(String username, String password) throws InterruptedException {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open(baseUrl);
		InventoryPage inventoryPage = loginPage.login(username, password);
		Assert.assertTrue(inventoryPage.getCurrentUrl().contains("inventory.html"),
				"Login failed: Inventory page URL not found");
		Assert.assertEquals(inventoryPage.getPageTitle(), "Products");
		Thread.sleep(1000);
	}

	@Test(groups = { "smoke", "regression" })
	public void validLoginShouldOpenSecurePage() throws InterruptedException {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open(baseUrl);
		InventoryPage inventoryPage = loginPage.login("standard_user", "secret_sauce");
//		inventoryPage.openSidebar();
//		Thread.sleep(1000);
//		inventoryPage.closeSidebar();
//		Thread.sleep(2000);
//		loginPage.open(baseUrl).login("standard_user", "secret_sauce").openSidebar();

		Assert.assertTrue(inventoryPage.getCurrentUrl().contains("inventory.html"),
				"Login failed: Inventory page URL not found");
		Assert.assertEquals(inventoryPage.getPageTitle(), "Products");

		inventoryPage.testActions();

		Thread.sleep(4000);
	}

	@Test(groups = { "regression" })
	public void invalidLoginShouldDisplayError() {
		LoginPage loginPage = new LoginPage(driver);
		loginPage.open(baseUrl);
		loginPage.login("locked_out_user", "secret_sauce");
		Assert.assertEquals(loginPage.isErrorOccured(), true);

	}
}
