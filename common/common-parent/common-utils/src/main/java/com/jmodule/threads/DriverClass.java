package com.jmodule.threads;

/**
 * <p>
 * As a brief not this is the starting point of All thread based implementation
 * 
 * </p>
 * 
 * @author vrengasamy
 *
 */
public class DriverClass {
	public static void main(String[] args) {
		Thread t = new SampleThread("Effective JAVA");
		Thread t2 = new Thread(new SampleRunnable("Effective JAVA2"));
		t.start();
		t2.start();
	}
}
