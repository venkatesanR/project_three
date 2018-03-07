package com.datastructures.search;

public class DriverClass {
	public static void main(String[] args) {
		int k=1;
		int[] a={1, 2, 3 ,4 ,5};
		System.out.println(hackerLandTransmitors(a,k));
	}

	private static int hackerLandTransmitors(int[] data, int k) {
		int current = data[0];
		int count = 0;
		int eCount = 0;
		int elementryCount = 0;
		if (data.length > 1) {
			for (int index = 1; index < data.length; index++) {
				int op = (data[index] / (k * current));
				if (op > 1) {
					count += 1;
					current = data[index];
				} else if (op == 1) {
					elementryCount += 1;
				}
			}
			eCount = (int) Math.floor(elementryCount / 2);
		}
		return count + eCount;
	}
}
