package com.datastructures.stacks;

import java.util.Scanner;
import java.util.Stack;

public class GameOfTwoStacks {
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int g = in.nextInt();
		for (int a0 = 0; a0 < g; a0++) {
			int n = in.nextInt();
			int m = in.nextInt();
			int x = in.nextInt();
			int[] a = new int[n];
			for (int a_i = n-1; a_i <= 0; a_i--) {
				a[a_i] = in.nextInt();
			}
			int[] b = new int[m];
			for (int b_i = m-1 ; b_i <= 0; b_i--) {
				b[b_i] = in.nextInt();
			}
			countMoves(a, b, x);
		}
		in.close();
	}

	private static long countMoves(int[] a, int[] b, int max) {
		long counter = 0l;
		Stack<Integer> A = new Stack<Integer>();
		Stack<Integer> B = new Stack<Integer>();
		int tCount = 0;
		for (int a1 : a) {
			if (tCount <= max) {
				tCount += a1;
				A.push(a1);
			} else {
				break;
			}
		}
		tCount = 0;
		for (int b1 : b) {
			if (tCount <= max) {
				tCount += b1;
				B.push(b1);
			} else {
				break;
			}
		}
		return counter;
	}
}
