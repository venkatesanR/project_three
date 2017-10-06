package com.datastructures.arrays;

public class Rotations {

	/***
	 * time complexity =O(n) ,space= O(shift)
	 * 
	 * @param arr
	 * @param shift
	 */
	public static void spaciousRotation(int[] arr, int shift) {
		// Initialize temp array with knumber of elements to shift
		int[] temp = new int[shift];
		// move all data to shift to temp
		for (int i = 0; i < shift; i++) {
			temp[i] = arr[i];
		}
		// shift all rest of the data by left
		for (int j = shift; j < arr.length; j++) {
			arr[j - shift] = arr[j];
		}
		// merge final array
		for (int k = arr.length - shift; k < arr.length; k++) {
			// 7-2=4
			arr[k] = temp[k - (arr.length - shift)];
		}
	}

	public static void rotateByOne() {

	}

	public static void reversalRotation(int[] arr, int shift) {
		swap(arr, 0, shift - 1);
		swap(arr, shift, arr.length - 1);
		swap(arr, 0, arr.length - 1);
	}

	private static void swap(int[] arr, int start, int end) {
		int temp;
		while (start < end) {
			temp = arr[start];
			arr[start] = arr[end];
			arr[end] = temp;
			start++;
			end--;
		}
	}
}
