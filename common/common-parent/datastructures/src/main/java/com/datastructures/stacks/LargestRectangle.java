package com.datastructures.stacks;

import java.util.Scanner;
import java.util.Stack;

public class LargestRectangle {
	static long largestRectangle(int[] h) {
		Stack<Building> buildings = new Stack<Building>();
		for (int i = 1; i <= h.length; i++) {
			// push if empty
			if (buildings.isEmpty()) {
				buildings.push(new Building(h[i - 1]));
			} else {
				Building previous = buildings.peek();
				
			}

		}
		return 0;
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

	static class Building {
		int data;
		int count;
		long area;

		public Building(int data) {
			this.data = data;
			this.count = 1;
		}

		public void increment() {
			this.count += 1;
		}

		public long area() {
			return count * data;
		}
	}
}
