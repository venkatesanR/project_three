package com.techland.training.krishna.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

public class AccountCreation {
	private static WebDriver Driver;
	
	@FindBy(how = How.XPATH, using = "//*[@id='link-signup']/a")			private static WebElement signup;
	@FindBy(how = How.XPATH, using = "//*[@id='FirstName']")				private static WebElement FirstName;
	@FindBy(how = How.XPATH, using = "//*[@id='LastName']")					private static WebElement LastName;
	@FindBy(how = How.XPATH, using = "//*[@id='GmailAddress']")				private static WebElement MailId;
	@FindBy(how = How.XPATH, using = "//*[@id='Passwd']")					private static WebElement Password;
	@FindBy(how = How.XPATH, using = "//*[@id='PasswdAgain']")				private static WebElement ConfirmPassword;
	@FindBy(how = How.XPATH, using = "//div[@title='Birthday']")			private static WebElement MonthList;
	@FindBy(how = How.XPATH, using = "//div[@id=':7']")						private static WebElement Month;
	@FindBy(how = How.XPATH, using = "//*[@id='BirthDay']")					private static WebElement Date;
	@FindBy(how = How.XPATH, using = "//*[@id='BirthYear']")				private static WebElement Year;
	@FindBy(how = How.XPATH, using = "//div[@title='Gender']")				private static WebElement GenderList;
	@FindBy(how = How.XPATH, using = "//div[@id=':f']/div")					private static WebElement Gender;
	@FindBy(how = How.XPATH, using = "//*[@id='RecoveryPhoneNumber']")		private static WebElement PhoneNumber;
	
	public AccountCreation(WebDriver driver) {
		AccountCreation.Driver = driver;
		PageFactory.initElements(Driver, this);
	}

	public void accountCreation() {
		if (!"Gmail".equals(Driver.getTitle())) {
			Driver.get("http://accounts.google.com");
			Driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		}

		signup.click();
		Driver.switchTo().defaultContent().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		Assert.assertEquals(Driver.getTitle(), "Create your Google Account");
		FirstName.sendKeys("Mohana");
		LastName.sendKeys("Krishnan");
		MailId.sendKeys("naveenkrishna9952");
		Password.sendKeys("9952917661");
		ConfirmPassword.sendKeys("9952917661");
		MonthList.click();
		Month.click();
		Date.sendKeys("05");
		Year.sendKeys("1992");
		GenderList.click();
		Gender.click();
		PhoneNumber.clear();
		PhoneNumber.sendKeys("+91 9952917661");
		
		/**
		 * java script executor
		 *
		 *	JavascriptExecutor js = (JavascriptExecutor) driver;
		 *	try {
		 *		String value = (String) js.executeScript("return document.getElementById('RecoveryPhoneNumber').value");
		 *		value = value + " 9652115425";
		 *		js.executeScript("document.getElementById('RecoveryPhoneNumber').value='+' " + value );
		 *	}catch (Exception e) {
		 *		e.printStackTrace();
		 *	}
		 *
		 */
	}
}
