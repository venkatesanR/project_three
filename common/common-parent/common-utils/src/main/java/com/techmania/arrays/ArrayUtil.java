package com.techmania.arrays;

public class ArrayUtil {
	private ArrayUtil() {

	}

	public static void main(String[] args) {
		int[] input = new int[] { 1, 2, 3, 4, 5, 6 };
		int shift = 5;
		rotate(input, shift);
		for (int i = 0; i < input.length; i++) {
			System.out.println(input[i]);
		}
	}

	/**
	 * consider input -> 1,2,3,4,5,6 </br>
	 * Example: rotate by 2 -> 3,4,5,6,1,2
	 * 
	 * @param arr
	 * @param numShift
	 */
	public static void rotate(int[] arr, int numShift) {
		int[] temp = new int[numShift];
		// rotate left
		for (int i = 0; i < numShift; i++) {
			temp[i] = arr[i];
		}
		// Shift By Num Shift
		for (int j = numShift; j < arr.length; j++) {
			arr[j - numShift] = arr[j];
		}
		for (int k = (arr.length - numShift); k < arr.length; k++) {
			arr[k] = temp[(k - (arr.length - numShift))];
		}
	}
}
