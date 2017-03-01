package com.techland.training.krishna.selenium;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Stock {

	WebDriver driver;
	WebDriverWait wait;
	
	@FindBy (how = How.ID, using = "keyword") private WebElement searchbox;
	@FindBy (xpath="//span[@id='ajax_response']/ol/li/a/b/span[@class='symbol' and text()='CAIRN']/ancestor::a") private WebElement searchlist;
	
	@BeforeTest
	public void setUp(){
		System.setProperty("webdriver.gecko.driver","C:\\Users\\mvenkatesan@yume.com\\workspace\\Selenium\\lib\\geckodriver-v0.14.0-win64\\geckodriver.exe");
		driver = new FirefoxDriver();
		wait = new WebDriverWait(driver, 30);
		PageFactory.initElements(driver, this);
		driver.get("https://www.nseindia.com/index_nse.htm");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='wrapper']/div[1]/a/img")));
		Assert.assertEquals(driver.getTitle(), "NSE - National Stock Exchange of India Ltd.");
	}
	
	@Test
	public void details() throws InterruptedException{
		searchbox.clear();
		searchbox.sendKeys("CAIRN");
		Thread.sleep(3000);
		searchlist.click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("symbol")));
		Assert.assertEquals(driver.findElement(By.id("symbol")).getText(), "CAIRN");
		System.out.println("traded volume  " + driver.findElement(By.xpath("//*[@id='tradedVolume']")).getText());
		driver.findElement(By.xpath("//*[@id='show-hide']/h5[1]")).click();
		driver.findElement(By.xpath("//*[@id='show-hide']/h5[2]")).click();
		System.out.println("Var margin  " + driver.findElement(By.xpath("//*[@id='varMargin']")).getText());
		System.out.println("Market Capital  " + driver.findElement(By.xpath("//*[@id='ffmid']")).getText());
		System.out.println("today stock value  " + driver.findElement(By.xpath("//*[@id='lastPrice']")).getText());
		System.out.println("52 week high value  " + driver.findElement(By.xpath("//*[@id='high52']/font")).getText());
		System.out.println("52 week low value  " + driver.findElement(By.xpath("//*[@id='low52']/font")).getText());
		driver.findElement(By.xpath("//*[@id='tab6']")).click();
		Thread.sleep(3000);
		System.out.println("EPS value  " + driver.findElement(By.xpath("//*[@id='tab20Content']/table/tbody/tr[5]/td[2]")).getText());
		System.out.println("Divident value  " + driver.findElement(By.xpath("//*[@id='cac11']")).getText());
	}
	
	@AfterTest
	public void tearDown(){
		driver.quit();
	}
	
	
	
	
	public void readFromFile() throws IOException {
		BufferedReader input = null;
		input = new BufferedReader(new FileReader("E:\\inputfile.txt"));
		String line;
		while ((line = input.readLine()) != null) {
			System.out.println(line);
		}
		input.close();
	}
	
	public void writeInCsv() throws IOException{
		PrintWriter write = new PrintWriter(new FileWriter("E:\\output.csv"));
		StringBuilder sb = new StringBuilder();
		sb.append("id");
		sb.append(",");
		sb.append("name");
		sb.append("\n");
		sb.append("1");
		sb.append(",");
		sb.append("krishna");
		write.write(sb.toString());
		write.close();
		System.out.println("done");
	}
	
	
	public static void main(String[] args) {
		Stock stock = new Stock();
		try {
//			stock.readFromFile();
//			stock.writeInCsv();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
