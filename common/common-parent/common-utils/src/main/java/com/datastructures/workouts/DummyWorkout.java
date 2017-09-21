package com.datastructures.workouts;

public class DummyWorkout {
	public static void main(String[] args) {
		int k = 3;
		int[] array = new int[k];
		print(array);
		for (int i = 0; i < k; i++) {
			recurse(new int[k], i);
		}
	}

	public static void recurse(int[] array, int pivot) {
		array[pivot] = 1;
		print(array);
		for (int i = pivot + 1; i < array.length; i++) {
			recurse(array, i);
			array[i] = 0;
		}
	}

	private static void print(int[] array) {
		StringBuffer buffer = new StringBuffer();
		for (int i : array) {
			buffer.append(i);
		}
		System.out.println(buffer.toString());
	}
}
