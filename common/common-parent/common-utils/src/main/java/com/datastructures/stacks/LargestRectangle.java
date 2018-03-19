package com.datastructures.stacks;

import java.util.Scanner;
import java.util.Stack;

public class LargestRectangle {
	static long largestRectangle(int[] h) {
		Stack<Long> rectangleSize = new Stack();
		int size = h.length;
		int lastHeight = h[0];
		for (int i = 0; i < h.length; i++) {
			int height = h[i];
			int localIteration = (height - lastHeight);
			if (localIteration == 0) {
				if (rectangleSize.isEmpty() || (!rectangleSize.isEmpty() && rectangleSize.peek() < height * size)) {
					rectangleSize.push(new Long(height * size));
				}
			} else {
				for (int j = 1; j <= localIteration; j++) {
					int localHeight = lastHeight + j;
					if (rectangleSize.isEmpty()) {
						rectangleSize.push(new Long(localHeight * size));
					}
					if (rectangleSize.peek() < localHeight * size) {
						rectangleSize.push(new Long(localHeight * size));
					}
				}
			}

			lastHeight = height;
			size -= 1;
		}
		return rectangleSize.peek();
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		int n = in.nextInt();
		int[] h = new int[n];
		for (int h_i = 0; h_i < n; h_i++) {
			h[h_i] = in.nextInt();
		}
		long result = largestRectangle(h);
		System.out.println(result);
		in.close();
	}
}
