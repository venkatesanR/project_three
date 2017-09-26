package com.techmania.datastructures;

public class UtilFunctions {
	static int[] data = new int[4];

	private UtilFunctions() {

	}

	public static void main(String[] args) {
		binaryString(4);
	}

	public static long backTrack(long input) {
		if (input == 0) {
			return input;
		} else {
			System.out.println(input);
			return backTrack(input - 1);
		}
	}

	public static long fact(long input) {
		if (input == 0 || input == 1) {
			return 1;
		} else {
			return input * fact(input - 1);
		}
	}

	// backtracking

	public static void binaryString(int n) {
		if (n < 1)
			System.out.println(printArray());
		else {
			data[n - 1] = 0;
			binaryString(n - 1);
			data[n - 1] = 1;
			binaryString(n - 1);
		}
	}
	
	static String printArray() {
		StringBuilder builder=new StringBuilder();
		for(int i=data.length-1;i >= 0;i--) {
			builder.append(String.valueOf(data[i]));
		}
		return builder.toString();
	}
}
