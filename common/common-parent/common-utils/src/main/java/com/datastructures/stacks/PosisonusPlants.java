package com.datastructures.stacks;

import java.util.Collections;
import java.util.Scanner;
import java.util.Stack;

public class PosisonusPlants {
	private static Stack<Integer> plants = new Stack();

	public static void main(String[] args) {
		int n = 10;
		int[] p = {2 ,1, 3, 1 ,4 ,2 ,1, 4, 3 ,3};
		for (int p_i = 0; p_i < n; p_i++) {
			plants.push(p[p_i]);
		}
		int result = poisonousPlants(plants,p);
		System.out.println(result);
	}

	static int poisonousPlants(Stack<Integer> plants,int[] p) {
		int dayCount = 0;
		int diedCount = -1;
		while (diedCount != 0) {
			diedCount = diedPlants();
			if (diedCount == 0)
				break;
			dayCount += 1;
		}
		return dayCount;
	}

	private static int diedPlants() {
		int diedCount = 0;
		int right = plants.pop();
		int size = plants.size();
		int count = 0;
		int left = -1;
		Stack<Integer> tempStack = new Stack();
		while (count != size && !plants.isEmpty()) {
			left = plants.peek();
			if (right > left) {
				diedCount += 1;
				right = left;
				if (!plants.isEmpty())
					left = plants.pop();
			} else {
				tempStack.push(right);
				if (!plants.isEmpty())
					right = plants.pop();
			}
			count += 1;
		}
		tempStack.push(left);
		Collections.reverse(tempStack);
		plants = tempStack;
		return diedCount;
	}
}
