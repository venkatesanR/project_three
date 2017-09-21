package co.datastructures.recursion;

import java.math.BigInteger;

public class RecursionBacktracking {
	static int[] data = null;
	private static int MAX_VALUE = 10;

	private RecursionBacktracking() {

	}

	public static void main(String[] args) {
		System.out.println(fact(new BigInteger("10")));
	}

	/**
	 * Finds factorial of an given number <input> call's recursively until all
	 * methods stacks find its own fact number 
	 * fact(n)={ 
	 *         n*fact(n-1) n > 0;
	 *         1           n =0;
	 * 
	 * @param input
	 * @return
	 */
	public static BigInteger fact(BigInteger input) {
		BigInteger result = new BigInteger("1");
		if (input.equals(BigInteger.ZERO) || input.equals(BigInteger.ONE)) {
			return result;
		} else {
			return input.multiply(fact(input.subtract(BigInteger.ONE)));
		}
	}

	/**
	 * This method used to find for an given array is sorted or not By calling
	 * recursively by decrement array index and
	 * 
	 * @param a
	 * @param n
	 * @return
	 */
	public static boolean isSorted(int[] a, int n) {
		if (n == 1) {
			return true;
		} else {
			return (a[n - 1] < a[n - 2] ? false : isSorted(a, n - 1));
		}
	}

	/**
	 * This util method used to generate binary numbers Eg: given data is 3
	 * digit long(with base of 2 Binary) Output should be 000 001 010 011 100
	 * 101 110 111 Using backtracking logic we can achieve this output consider
	 * size 3 array contains all 3 zero's initial i1: time complexity O(n)=2^n
	 * 
	 * @param length
	 */
	public static void generateBinary(int length) {
		if (length < 1) {
			print(data);
		} else {
			// consider input array here is Global variable to fix
			data[length - 1] = 0;
			generateBinary(length - 1);
			data[length - 1] = 1;
			generateBinary(length - 1);
		}
	}

	/**
	 * Actually Idea here was to implement recursive solution
	 * to Identify serious count as mentioned below.
	 * N=Actual Number
	 * k=power
	 * N=a^k+b^k+...
	 * prove(if k=2)
	 *  10=1^2+3^2=10(count=2)
	 * @param result
	 * @param power
	 * @return
	 */
	public static int powerSum(int result, int power,int expected) {
		if(result==expected) {
			
		} else if(result > expected) {
			
		} else {
			
		}
		return 0;
	}

	public static void printFibbonaci(int max) {
		for (int i = 0; i < max; i++) {
			System.out.println(getFibbonaci(i));
		}
	}

	public static int getFibbonaci(int n) {
		if (n < 2) {
			return n;
		} else {
			return getFibbonaci(n - 1) + getFibbonaci(n - 2);
		}
	}

	/**
	 * Draw K numbers from N Sized array permutations time complexity O(n)=k^n
	 * 
	 * @param n
	 * @param k
	 */
	public static void drawKNumbers(int n, int k) {
		if (n < 1) {
			print(data);
		} else {
			for (int j = 0; j < k; j++) {
				data[n - 1] = j;
				drawKNumbers(n - 1, k);
			}
		}
	}

	// Merges two subarrays of arr[].
	// First subarray is arr[l..m]
	// Second subarray is arr[m+1..r]
	void merge(int arr[], int l, int m, int r) {
		// Find sizes of two subarrays to be merged
		int n1 = m - l + 1;
		int n2 = r - m;

		/* Create temp arrays */
		int L[] = new int[n1];
		int R[] = new int[n2];

		/* Copy data to temp arrays */
		for (int i = 0; i < n1; ++i)
			L[i] = arr[l + i];
		for (int j = 0; j < n2; ++j)
			R[j] = arr[m + 1 + j];

		/* Merge the temp arrays */

		// Initial indexes of first and second subarrays
		int i = 0, j = 0;

		// Initial index of merged subarry array
		int k = l;
		while (i < n1 && j < n2) {
			if (L[i] <= R[j]) {
				arr[k] = L[i];
				i++;
			} else {
				arr[k] = R[j];
				j++;
			}
			k++;
		}

		/* Copy remaining elements of L[] if any */
		while (i < n1) {
			arr[k] = L[i];
			i++;
			k++;
		}

		/* Copy remaining elements of R[] if any */
		while (j < n2) {
			arr[k] = R[j];
			j++;
			k++;
		}
	}

	// Main function that sorts arr[l..r] using
	// merge()
	void sort(int arr[], int l, int r) {
		if (l < r) {
			// Find the middle point
			int m = (l + r) / 2;

			// Sort first and second halves
			sort(arr, l, m);
			sort(arr, m + 1, r);

			// Merge the sorted halves
			merge(arr, l, m, r);
		}
	}

	/* A utility function to print array of size n */
	static void printArray(int arr[]) {
		int n = arr.length;
		for (int i = 0; i < n; ++i)
			System.out.print(arr[i] + " ");
		System.out.println();
	}

	private static void print(int[] a) {
		StringBuilder builder = new StringBuilder();
		for (int i = a.length - 1; i >= 0; i--) {
			builder.append(a[i]);
		}
		System.out.println(builder.toString());
	}
}
