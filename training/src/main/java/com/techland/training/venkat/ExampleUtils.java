package com.techland.training.venkat;

import java.util.Scanner;

public class ExampleUtils {
	public static void main(String args[]) {
		Scanner scanner=new Scanner(System.in);
		System.out.println("Enter radius :");
		double radius=scanner.nextDouble();
		while(true) {
			System.out.println("Area of a circle :"+calculateArea(radius));
		}
	}
	
	/**
	 * Area of a circle = pi * radius * radius
	 * @param radius
	 * @return
	 */
	private static double calculateArea(double radius) {
		return Math.PI * radius * radius;
	}

	private static void reArrange(char[] data,String matcher) {
		String response = "";
		for (int index = 0; index < data.length; index++) {
			int frequency = frequency(data, data[index], index);
			if ((frequency > 0 && frequency % 2 != 0)) {
				response = response.concat(String.valueOf(data[index]));
			} else if ((frequency > 0 && frequency % 2 == 0)) {
				index = index + frequency - 1;
			}
		}
		if(!matcher.equals(response)) {
			reArrange(response.toCharArray(),response);
		}else {
			System.out.println(response);
		}
	}

	private static int frequency(char[] data, char input, int pos) {
		int count = 0;
		for (int index = pos; index < data.length; index++) {
			if ((input ^ data[index]) == 0) {
				count++;
			} else {
				break;
			}
		}
		return count;
	}
}
