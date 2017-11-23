package com.datastructures.arrays;

import java.util.Arrays;

public class SerachAndSort {
	public static void main(String[] args) {
		int[] data={4,3,2,1,8};
		//bubleSort(data);
		//selectionSort(data);
		insertionSort(data);
		print(data);
	}
	// Sorting
	// 1.Bubble sort O(n)=n2
	public static void bubleSort(int[] data) {
		for (int pass = data.length - 1; pass >= 0; pass--) {
			for(int j=0;j<pass;j++) {
				if(data[j]>data[j+1]) {
					int temp=data[j];
					data[j]=data[j+1];
					data[j+1]=temp;
				}
			}
		}
	}
    
	//selection sort
	public static void selectionSort(int[] data) {
		for (int i = 0; i < data.length; i++) {
			int min = i;
			for (int j = i+1; j < data.length - 1; j++) {
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
	//selection sort
	public static void insertionSort(int[] data) {
		int v = 0, j = 0;
		for (int i = 1; i < data.length; i++) {
			j=i;
			v = data[i];
			while (j >= 1 && data[j - 1] > v) {
				data[j] = data[j-1];
				j--;
			}
			data[j] = v;
		}
	}
	// 3.Merge Sort

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

	// utility methods

	public static int pivotElement(int[] a, int low, int high) {
		int pivot = pivot(low, high);
		return a[pivot];
	}

	public static int pivot(int low, int high) {
		return (high + low) / 2;
	}

	public static void print(int[] data) {
		for (int i : data) {
			System.out.print(i + " ");
		}
	}

}
