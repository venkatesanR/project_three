package com.datastructures.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.techmania.common.util.KeyValue;

public class Rotations {

	/***
	 * time complexity =O(n) ,space= O(shift)
	 * 
	 * @param arr
	 * @param shift
	 */
	public static void spaciousRotation(int[] arr, int shift) {
		// Initialize temp array with knumber of elements to shift
		int[] temp = new int[shift];
		// move all data to shift to temp
		for (int i = 0; i < shift; i++) {
			temp[i] = arr[i];
		}
		// shift all rest of the data by left
		for (int j = shift; j < arr.length; j++) {
			arr[j - shift] = arr[j];
		}
		// merge final array
		for (int k = arr.length - shift; k < arr.length; k++) {
			// 7-2=4
			arr[k] = temp[k - (arr.length - shift)];
		}
	}

	public static void rotateByOne() {

	}

	public static void reversalRotation(int[] arr, int shift) {
		swap(arr, 0, shift - 1);
		swap(arr, shift, arr.length - 1);
		swap(arr, 0, arr.length - 1);
	}

	private static void swap(int[] arr, int start, int end) {
		int temp;
		while (start < end) {
			temp = arr[start];
			arr[start] = arr[end];
			arr[end] = temp;
			start++;
			end--;
		}
	}

	// Function returns the minimum number of swaps
	// required to sort the array
	public static int minSwaps(int[] popularity) {
		List<KeyValue<Integer, Integer>> list = new ArrayList<>();
		int index = 0;
		for (int input : popularity) {
			KeyValue<Integer, Integer> kv = new KeyValue<Integer, Integer>(input, index);
			list.add(kv);
			index = index + 1;
		}

		Collections.sort(list, (o1, o2) -> {
			if (o1.getKey() < o2.getKey()) {
				return 1;
			} else if (o1.getKey().equals(o2.getKey())) {
				return 0;
			} else {
				return -1;
			}
		});

		// sort by input

		int count = 0;
		Boolean[] visited = new Boolean[popularity.length];
		Arrays.fill(visited, false);

		for (int i = 0; i < popularity.length; i++) {
			// not visited,given index
			if (visited[i] == true || list.get(i).getValue() == i) {
				continue;
			}
			int loop = 0;
			int j = i;
			while (!visited[j]) {
				visited[j] = true;
				j = list.get(j).getValue();
				loop++;
			}
			count += (loop - 1);
		}
		return count;
	}
}
