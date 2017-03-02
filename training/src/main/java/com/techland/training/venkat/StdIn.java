package com.techland.training.venkat;

import java.util.Scanner;

public class StdIn {

	private static Scanner scanner = new Scanner(System.in);

	private StdIn() {

	}

	public static String getString() {
		return scanner.next();
	}

	public static int getInt() {
		return scanner.nextInt();
	}

	public static double getDouble() {
		return scanner.nextDouble();
	}

	public static double getFloat() {
		return scanner.nextFloat();
	}

}
