package com.datastructures.arrays;

public class DriverClass {
	public static void main(String[] args) {
		int shift = 2;
		int[] input = new int[] { 6, 8, 10, 12, 5, 8, 9, 10, 8, 7, 20 ,4,8};
		Statistics.maxProfitDays(input);
	}

	private static String print(int[] input) {
		StringBuilder builder = new StringBuilder();
		for (int data : input) {
			builder.append(data).append(',');
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}
}
