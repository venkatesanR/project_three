package com.techmania.java8;

public class Impl1 implements I1,I2 {

	@Override
	public String getName() {
		return "Yours I1";
	}

	/*@Override
	public void print() {
		System.out.println("Hell I2");
	}*/

	@Override
	public void print() {
		I2.super.print();
	}
	
	public static void main(String[] args) {
		I1 one = new Impl1();
		I2 two = new Impl1();
		System.out.println(one.getName());
		System.out.println(two.getName());
	}
}
