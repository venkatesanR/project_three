package com.techmania.workouts;

import java.math.BigInteger;

public class FunctionUtils {
	static int[] data = null;
	private static int MAX_VALUE = 10;

	private FunctionUtils() {

	}

	public static void main(String[] args) {
		int length = 3;
		data = new int[] { 1, 2, 3 };
		printFibbonaci(10);
	}

	/**
	 * Finds factorial of an given number <input> call's recursively until all
	 * methods stacks find its own fact number fact(n)={ n*fact(n-1) n > 0; 1 n
	 * =0;
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

	public static int powerSum(int result, int power) {
		// TODO workouts needed to solve hackerrank problem
		return 0;
	}

	public static void printFibbonaci(int max) {
		for(int i=0;i<max;i++) {
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

	private static void print(int[] a) {
		StringBuilder builder = new StringBuilder();
		for (int i = a.length - 1; i >= 0; i--) {
			builder.append(a[i]);
		}
		System.out.println(builder.toString());
	}
}
