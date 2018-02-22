package com.datastructures.stacks;

import java.util.Stack;

public class PosisonusPlants {
	private static int dayCount = 0;

	public static void main(String[] args) {
		int n = 7;
		int[] p = { 6, 5, 8, 4, 7, 10, 9 };
		// n = 5;
		// int[] p = { 1, 4, 2, 3, 1 };
		Stack<Integer> plants = new Stack();
		for (int p_i = 0; p_i < n - 1; p_i++) {
			plants.push(p[p_i]);
		}
		int result = poisonousPlants(plants, p[p.length - 1], true);
		System.out.println(result);
	}

	private static int poisonousPlants(Stack<Integer> plants2, int right, boolean matchRight) {
		Stack<Integer> temp = new Stack<>();
		int diedCount = 0;
		while (!plants2.isEmpty()) {
			if (matchRight) {
				if (plants2.peek().intValue() < right) {
					right = plants2.pop();
					dayCount += 1;
					diedCount += 1;
				} else {
					temp.add(right);
					right = plants2.pop();
				}
			} else {
				if (plants2.peek().intValue() > right) {
					plants2.pop();
					dayCount += 1;
					diedCount += 1;
				} else {
					temp.add(right);
					right = plants2.pop();
				}
			}
		}
		temp.add(right);
		if (diedCount > 0 && !temp.isEmpty()) {
			poisonousPlants(temp, temp.pop(), !matchRight);
		}
		return dayCount;
	}

}
