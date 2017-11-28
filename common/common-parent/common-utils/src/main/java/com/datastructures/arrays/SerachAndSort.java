package com.datastructures.arrays;

import java.util.Arrays;

public class SerachAndSort {
	public static void main(String[] args) {
		int[] data = { 9, 8, 7, 6, 5 };
		// bubleSort(data);
		// selectionSort(data);
		// insertionSort(data);
		mergeSort(data, 0, data.length - 1);
		print(data);
	}

	// Sorting
	// 1.Bubble sort O(n)=n2
	public static void bubleSort(int[] data) {
		for (int pass = data.length - 1; pass >= 0; pass--) {
			for (int j = 0; j < pass; j++) {
				if (data[j] > data[j + 1]) {
					int temp = data[j];
					data[j] = data[j + 1];
					data[j + 1] = temp;
				}
			}
		}
	}

	// selection sort
	public static void selectionSort(int[] data) {
		for (int i = 0; i < data.length; i++) {
			int min = i;
			for (int j = i + 1; j < data.length - 1; j++) {
				if (data[j] < data[min]) {
					min = j;
				}
			}
			int temp = data[min];
			data[min] = data[i];
			data[i] = temp;
		}
	}

	// 3.Insertion sort
	// selection sort
	public static void insertionSort(int[] data) {
		int v = 0, j = 0;
		for (int i = 1; i < data.length; i++) {
			j = i;
			v = data[i];
			while (j >= 1 && data[j - 1] > v) {
				data[j] = data[j - 1];
				j--;
			}
			data[j] = v;
		}
	}
	// 3.Merge Sort

	public static void mergeSort(int[] input, int low, int high) {
		if (low < high) {
			int pivot = pivot(low, high);
			mergeSort(input, low, pivot);
			mergeSort(input, pivot + 1, high);
			merge(input, low, pivot, high);
		}
	}

	private static void merge(int[] input, int low, int mid, int high) {
		int i = 0, j = 0, k = 0;
		int left_size = (mid - low) + 1;
		int right_size = high - mid;

		// create temp arrays
		int[] l = new int[left_size];
		int[] r = new int[right_size];
		for (int a = 0; a < left_size; a++) {
			l[a] = input[low + a];
		}
		for (int b = 0; b < right_size; b++) {
			r[b] = input[mid + 1 + j];
		}
		/* Merge the temp arrays back into arr[l..r] */
		i = 0; // Initial index of first subarray
		j = 0; // Initial index of second subarray
		k = low; // Initial index of merged subarray
		while (i < left_size && j < right_size) {
			if (l[i] < r[j]) {
				input[k] = l[i];
				i += 1;
			} else {
				input[k] = r[j];
				j += 1;
			}
			k += 1;
		}

		//
		while (i < left_size) {
			input[k] = l[i];
			i += 1;
			k += 1;
		}
		while (j < right_size) {
			input[k] = r[j];
			j += 1;
			k += 1;
		}
	}
	// 4.Quick sort

	// Searching
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

	public static void insertIntoSorted(int[] ar) {
		int index = ar.length - 1;
		for (index = ar.length - 1; index > 0; index--) {
			if (ar[index - 1] > ar[index]) {
				ar[index] = ar[index - 1];
				print(ar);
			}
		}
	}

	public static int findIsland(int[][] a) {
		boolean[][] visited = new boolean[a.length][a.length];
		int count = 0;
		int sum = 0;
		int tempSum = 0;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a.length; j++) {
				tempSum = 0;
				if (!visited[i][j]) {
					if (a[i][j] == 1) {
						visited[i][j] = true;
						tempSum += a[i][j];
					}
				}
				if (j + 1 < a.length && !visited[i][j + 1]) {
					if (a[i][j] == 1) {
						visited[i][j + 1] = true;
						tempSum += a[i][j + 1];
					}
				}
				if (j - 1 > 0 && !visited[i][j - 1]) {
					if (a[i][j] == 1) {
						visited[i][j - 1] = true;
						tempSum += a[i][j - 1];
					}
				}
				if (i - 1 > 0 && !visited[i - 1][j]) {
					if (a[i][j] == 1) {
						visited[i - 1][j] = true;
						tempSum += a[i - 1][j];
					}
				}
				if (i - 1 > 0 && j + 1 < a.length && !visited[i - 1][j + 1]) {
					if (a[i][j] == 1) {
						visited[i - 1][j + 1] = true;
						tempSum += a[i - 1][j + 1];
					}

				}
				if (i - 1 > 0 && j - 1 > 0 && !visited[i - 1][j - 1]) {
					if (a[i][j] == 1) {
						visited[i - 1][j - 1] = true;
						tempSum += a[i - 1][j - 1];
					}
				}
				if (i + 1 < a.length && !visited[i + 1][j]) {
					if (a[i][j] == 1) {
						visited[i + 1][j] = true;
						tempSum += a[i + 1][j];
					}
				}
				if (i + 1 < a.length && j + 1 < a.length && !visited[i + 1][j + 1]) {
					if (a[i][j] == 1) {
						visited[i + 1][j + 1] = true;
						tempSum += a[i + 1][j + 1];
					}
				}
				if (i + 1 < a.length && j - 1 > 0 && !visited[i + 1][j - 1]) {
					if (a[i][j] == 1) {
						visited[i + 1][j - 1] = true;
						tempSum += a[i + 1][j - 1];
					}
				}
				if (tempSum == 0 && sum > 0) {
					sum = 0;
					count += 1;
				} else {
					sum += tempSum;
				}
			}
		}
		return count + ((sum == 0 && tempSum > 0) ? 1 : 0);
	}

	
	/**
	 * Given an array A[] consisting 0s, 1s and 2s, write a function that sorts
	 * A[]. The functions should put all 0s first, then all 1s and all 2s in
	 * last.
	 * 
	 * Example Input = {0, 1, 1, 0, 1, 2, 1, 2, 0, 0, 0, 1}; Output = {0, 0, 0,
	 * 0, 0, 1, 1, 1, 1, 1, 2, 2}
	 */
	
	public static void countSort(int[] a) {
		// get the max value

		// Initialize auxialry array to zero

		// count each occurances
		
		//populate

	}
	// utility methods

	public static int pivotElement(int[] a, int low, int high) {
		int pivot = pivot(low, high);
		return a[pivot];
	}

	public static int pivot(int low, int high) {
		return low + ((high - low) / 2);
	}

	public static void print(int[] data) {
		for (int i : data) {
			System.out.print(i + " ");
		}
	}

}
