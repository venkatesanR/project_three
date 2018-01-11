package com.datastructures.arrays;

import com.datastructures.utils.ArrayUtils;

public class Combine {
	/**
	 * Consider the scenario of cricket match a batsman can score only Runs
	 * between {0,1,2,3,4,5,6} possible runs using this batsman need to Score X
	 * runs from Y Balls. What all the possibilities He/She can score.
	 * 
	 */

	private static final int[] RUNS = new int[] { 0, 1, 2, 3, 4, 5, 6 };
	private static int N;
	private static int M;
	private static int max=1000;

	public static void main(String[] args) {
		N = 8;
		M = 6;
		int[] posibility = new int[M];
		printAllCombinations(N, M, posibility);
		System.out.println(max);
	}

	private static void printAllCombinations(int N, int M, int[] posibility) {
		if (M == 0) {
			int sum = 0;
			for (int l : posibility) {
				sum += l;
			}
			if (max > sum) {
				max = sum;
			}
			System.out.println(ArrayUtils.print(posibility));
		} else {
			for (int i = 0; i < RUNS.length; i++) {
				posibility[M - 1] = RUNS[i];
				printAllCombinations(N, M - 1, posibility);
			}
		}
	}
}
