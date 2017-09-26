package com.datastructures.arrays;

public class Statistics {

	public static void maxProfitDays(int[] data) {
		int profit = 0;
		int min = 0;
		int max = 0;
		int previousMin=0;
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
	public static int findKthLargestSorted2D(int[][] data, int k, int n) {
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

	public static void main(String[] args) {
		int[][] data = { { 10, 20, 30, 40 }, { 50, 60, 70, 80 }, { 80, 90, 100, 120 }, { 120, 140, 150, 160 }, };
		System.out.println(findKthLargestSorted2D(data, 7, 4));

	}
}
