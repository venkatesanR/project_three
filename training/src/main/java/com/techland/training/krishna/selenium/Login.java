package com.techland.training.krishna.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class Login {
	private static WebDriver Driver;
	WebDriverWait wait ;
	
	@FindBy(how = How.XPATH, using = "//*[@id='account-chooser-link']") private static WebElement signInDifferentAccount;
	@FindBy(how = How.XPATH, using = "//*[@id='account-chooser-add-account']") private static WebElement addAccount;
	@FindBy(how = How.XPATH, using = "//*[@id='Email']") private static WebElement emailId;
	@FindBy(how = How.XPATH, using = "//*[@id='next']") private static WebElement nextButton;
	@FindBy(how = How.XPATH, using = "//*[@id='Passwd']") private static WebElement password;
	@FindBy(how = How.XPATH, using = "//*[@id='signIn']") private static WebElement signInButton;
	@FindBy(how = How.XPATH, using = "//*[@id='PersistentCookie']") private static WebElement staySignedIn;
	
	
	public Login(WebDriver driver){
		Login.Driver = driver;
		PageFactory.initElements(Driver, this);
		wait = new WebDriverWait(Driver,30);
	}
	
	public static void login() {
		if (!"Gmail".equals(Driver.getTitle())) {
			signInDifferentAccount.click();
			Driver.switchTo().defaultContent().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
			addAccount.click();
			Driver.switchTo().defaultContent().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		} else if ("Google Accounts".equals(Driver.getTitle())) {
			addAccount.click();
			Driver.switchTo().defaultContent().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		}else{
			Driver.get("http://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmail.google.com%2Fmail%2F&service=mail&ltmpl=default&dsh=-3606147151380171750#identifier");
		}
//		Assert.assertEquals(Driver.getTitle(), "Sign in - Google Accounts");
		emailId.sendKeys("mohanakrishnan0592");
		nextButton.click();
		Driver.switchTo().defaultContent().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
		password.sendKeys("ratisbon");
		signInButton.click();
	}
}
