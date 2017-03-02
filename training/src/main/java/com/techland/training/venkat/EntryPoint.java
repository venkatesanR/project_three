package com.techland.training.venkat;


public class EntryPoint {
	public static void main(String[] args) {
        Integer[] data = null;
        System.out.println("Enter total ElementsIn array");
        int N = StdIn.getInt();
        data = new Integer[N];
        for (int i = 0; i < N; i++) {
            data[i] = StdOut.uniform(0, N);
        }
        Sort sort = new Sort();
        GraphUtil.showGraph(data);
        // sort.selectionSort(data);
        sort.insertionSort(data);
        GraphUtil.showGraph(data);
	}
}
