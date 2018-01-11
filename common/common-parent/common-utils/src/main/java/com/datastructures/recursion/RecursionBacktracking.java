package com.datastructures.recursion;

import java.math.BigInteger;

import com.datastructures.utils.ArrayUtils;

public class RecursionBacktracking {
	static int[] data = { 0, 1, 2, 3, 4, 6 };

	private RecursionBacktracking() {

	}

	public static void main(String[] args) {
		// System.out.println(bigFibbo(5, 0, 1));
		drawKNumbers(data.length, 3);
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
			ArrayUtils.print(data);
		} else {
			// consider input array here is Global variable to fix
			data[length - 1] = 0;
			generateBinary(length - 1);
			data[length - 1] = 1;
			generateBinary(length - 1);
		}
	}

	/**
	 * Actually Idea here was to implement recursive solution to Identify
	 * serious count as mentioned below. N=Actual Number k=power N=a^k+b^k+...
	 * prove(if k=2) 10=1^2+3^2=10(count=2)
	 * 
	 * @param result
	 * @param power
	 * @return
	 */
	public static int powerSum(int result, int power) {
		return 0;
	}

	public static void printFibbonaci(int max) {

	}

	/**
	 * Draw K numbers from N Sized array permutations time complexity O(n)=k^n
	 * 
	 * @param n
	 * @param k
	 */
	public static void drawKNumbers(int n, int k) {
		if (n < 1) {
			System.out.println(ArrayUtils.print(data));
		} else {
			for (int j = 0; j < k; j++) {
				data[n - 1] = j;
				drawKNumbers(n - 1, k);
			}
		}
	}

	/**
	 * Improved Fibbonacci comming under Dynamic Programming
	 * 
	 * F(n) ={ 0 n=-2; 1 n=-1; Tn+Tn+1 Else where n >= 1
	 */
	public static BigInteger bigFibbo(int n, int a, int b) {
		BigInteger t1 = new BigInteger(String.valueOf(a));
		BigInteger t2 = new BigInteger(String.valueOf(b));
		BigInteger result = new BigInteger("0");
		int i = 3;
		while (i <= n) {
			result = t1.add(t2.multiply(t2));
			t1 = t2;
			t2 = result;
			i++;
		}
		return result;
	}

}
