package com.datastructures.arrays;

import java.util.Arrays;

public class SerachAndSort {
	public static int pivotElement(int[] a, int low, int high) {
		int pivot = pivot(low, high);
		return a[pivot];
	}

	public static int pivot(int low, int high) {
		return (high + low) / 2;
	}

	public static int search(int[] a, int k, boolean sorted) {
		if (a.length < 10) {
			for (int index = 0; index < a.length; index++) {
				if (k == a[index]) {
					return index;
				}
			}
		}
		return -1;
	}

	public static int binarySearch(int[] a, int low, int high, int k) {
		// Apply Binary Search
		int pivot = pivot(low, high);
		if (pivot < a.length && pivot >= 0) {
			if (k == a[pivot]) {
				return pivot;
			} else if (k < a[pivot]) {
				if (pivot <= 0) {
					return -1;
				}
				return binarySearch(a, 0, pivot - 1, k);
			} else if (k > a[pivot]) {
				if (pivot >= a.length) {
					return -1;
				}
				return binarySearch(a, pivot + 1, a.length, k);
			}
		}

		return -1;
	}

	/**
	 * sort an given array locate x(left extreme), locate y(right extreme)
	 * 
	 * @param a
	 * @param sum
	 */
	public static void pairExistance(int[] a, int sum) {
		Arrays.sort(a);
		int x = 0;
		int y = a.length - 1;
		int result;
		while (x < y) {
			result = a[x] + a[y];
			if (result < sum) {
				x++;
			} else if (result > sum) {
				y--;
			} else if (result == sum) {
				System.out.println("x= " + a[x] + "\t y=" + a[y] + "\t sum= " + sum);
				break;
			}
		}
	}

	public static void main(String[] args) {
		int[] a = { 1, 2, 3, 4, 5, 6, 7 };
		pairExistance(a, 12);
	}
}
