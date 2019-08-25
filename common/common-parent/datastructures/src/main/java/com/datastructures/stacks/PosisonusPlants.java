package com.datastructures.stacks;

import java.util.Stack;

/**
 * <p>
 * Editorial
 * <{@link https://www.hackerrank.com/challenges/poisonous-plants/editorial}>
 * Each plant must contain less or equal amount of pesticide than the plant to
 * it's left in order to survive. Maintain a stack where every element on the
 * stack contains the following information about a plant: the amount of
 * pesticide contained in the plant the number of days that have passed.
 * Initially, push onto the stack, where is the amount of pesticide contained in
 * the -th plant. Then iterate over the plants from left to right. Suppose that
 * the -th plant is now being considered. If the stack isn't empty, pop the top
 * element from the stack and compare it with . If is greater than this value,
 * then the -th plant will die on the first day. So add to the stack. Otherwise,
 * pop as long as the value in the top of the stack is greater than or equal to
 * . Update the answer with the second integer of the pair pushed onto the
 * stack. If the stack becomes empty then add to the stack otherwise add .
 * </p>
 */
public class PosisonusPlants {
	public static void main(String[] args) {
		int[] p = { 1, 4, 3, 2, 1 };
		System.out.println(processedPlants(p));
	}

	private static int processedPlants(int[] poison) {
		Stack<PlantDetails> plants = new Stack();
		int dayCount = 0;
		for (int i = 0; i < poison.length; i++) {
			if (plants.isEmpty()) {
				plants.push(new PlantDetails(poison[i], 0));
			} else {
				PlantDetails p1 = plants.peek();
				if (poison[i] > p1.poison) {
					dayCount = Math.max(dayCount, 1);
					plants.push(new PlantDetails(poison[i], 1));
				} else {
					PlantDetails p2 = plants.peek();
					int pday = p2.day;
					while (!plants.isEmpty() && p2.poison <= poison[i]) {
						plants.pop();
						if (plants.isEmpty()) {
							break;
						}
						pday = Math.max(pday, p2.day);
						p2 = plants.peek();
					}
					if (plants.empty()) {
						plants.push(new PlantDetails(poison[i], 0));
					} else {
						plants.push(new PlantDetails(poison[i], pday + 1));
						dayCount = Math.max(dayCount, pday + 1);
					}
				}
			}
		}
		return dayCount;
	}

	static class PlantDetails {
		int poison;
		int day;

		public PlantDetails(int poison, int day) {
			this.poison = poison;
			this.day = day;
		}

		@Override
		public String toString() {
			return (day + " " + poison);
		}
	}
}
