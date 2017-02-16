package com.techland.training.krishna.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Gmail {
	static WebDriver driver;
	
	@BeforeTest
	public static void setUp(){
		System.setProperty("webdriver.gecko.driver","C:\\Users\\mvenkatesan@yume.com\\workspace\\Selenium\\lib\\geckodriver-v0.14.0-win64\\geckodriver.exe");
		driver = new FirefoxDriver();
		driver.get("http://gmail.com");
		driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
	}
	
	@Test
	public static void test1(){
		try{
			AccountCreation acc = new AccountCreation(driver);
			acc.accountCreation(driver);	
		}catch(Exception ex){
			System.out.println(ex);
		}
		
	}
	
	@AfterTest
	public static void tearDown(){
		
	}
	
}
