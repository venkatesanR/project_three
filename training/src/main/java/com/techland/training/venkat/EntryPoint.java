package com.techland.training.venkat;

public class EntryPoint {

	public static void main(String[] args) throws InterruptedException {
		int count=0;
		while(count<=9) {
			doPrint();
			Thread.sleep(100);
			count=count+1;
		}
	}
	
	private static void doPrint() {
		System.out.println("Sasi");
	}
	
}

class Company{
	public Employee e;
	
}

class Employee{
	public String name;
}