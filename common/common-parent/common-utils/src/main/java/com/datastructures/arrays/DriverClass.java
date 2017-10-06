package com.datastructures.arrays;

import com.datastructures.utils.ArrayUtils;

public class DriverClass {
	public static void main(String[] args) {
		int[] input = new int[] { 6, 8, 10, 12, 5, 8, 9, 10, 8, 7, 20 ,4,8};
		Statistics.maxProfitDays(input);
		System.out.println(ArrayUtils.print(input));
	}
}
