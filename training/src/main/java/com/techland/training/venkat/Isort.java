package com.techland.training.venkat;

@SuppressWarnings("rawtypes")
public interface Isort {
	public void selectionSort(Comparable[] data);

	public void insertionSort(Comparable[] data);

	public void heapSort(Comparable[] data);

	public void bubbleSort(Comparable[] data);

	public void shellSort(Comparable[] data);
}
