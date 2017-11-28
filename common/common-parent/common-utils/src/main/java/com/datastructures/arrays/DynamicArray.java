package com.datastructures.arrays;

import java.util.List;

public class DynamicArray {

	private static void dynamicArray(int N, String[] querys, List<String> seqList) {
		int lastAnswer = 0;
		for (String query : querys) {
			String[] axy = query.split(" ");
			int a = Integer.valueOf(axy[0]);
			int x = Integer.valueOf(axy[1]);
			int y = Integer.valueOf(axy[2]);
			int seqIndex = ((x ^ lastAnswer) % N);
			String squence = seqList.get(seqIndex);
			if (a == 1) {
				
			} else {

			}
		}
	}
}
