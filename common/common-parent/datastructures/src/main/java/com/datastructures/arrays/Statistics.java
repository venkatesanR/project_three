package com.datastructures.arrays;

public class Statistics {

	public static void maxProfitDays(int[] data) {
		int profit = 0;
		int min = 0;
		int max = 0;
		for (int i = 1; i < data.length; i++) {
			if (data[min] < data[i] && profit < (data[i] - data[min])) {
				profit = data[i] - data[min];
				max = i;
			} else if (data[min] > data[i] && i != data.length - 1) {
				min = i;
			}
		}
		System.out.println("You can buy @ Rs:" + data[min] + "\t Sell @ Rs:" + data[max] + "\t Net profit Rs:"
				+ (data[max] - data[min]));
	}

	/**
	 * @param data
	 * @param k
	 * @return
	 */
	public static int finKth2D(int[][] data, int k, int n) {
		int row = 0;
		if (k > n) {
			row = (k / n);
		}
		int column = k % n;
		if (column == 0) {
			column = n - 1;
		} else {
			column = column - 1;
		}
		return data[row][column];
	}

	/***
	 * approach: 1.Sort 2.MinHeap 3.Bubble/Selection to findMax Bubble:O(k*n)
	 */
	public static int kthLargest(int[] a, int k) {
		// Bubble approach
		for (int i = 0; i < k; i++) {
			int max = i;
			for (int j = 1; j < a.length; j++) {
				if (a[j] > a[max]) {
					int temp = a[j];
					a[j] = a[max];
					a[max] = temp;
				}
			}
		}
		return a[k - 1];
	}
}
