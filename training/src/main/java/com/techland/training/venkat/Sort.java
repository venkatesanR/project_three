package com.techland.training.venkat;


public class Sort implements Isort {
	
	@LogRunTime
	public void selectionSort(Comparable[] data) {
        for (int index = 0; index < data.length; index++) {
            for (int recurence = index; recurence < data.length; recurence++) {
                if (SortUtil.less(data[recurence], data[index])) {
                	SortUtil.exch(data, index, recurence);
                }
            }
        }
	}
	
	@LogRunTime
	public void insertionSort(Comparable[] data) {
		for (int index = 1; index < data.length; index++) {
			for (int recurence = index; recurence > 0 && SortUtil.less(data[recurence], data[recurence - 1]); recurence--) {
				SortUtil.exch(data, recurence, recurence - 1);
			}
		}
	}

	public void heapSort(Comparable[] data) {

	}

	public void bubbleSort(Comparable[] data) {

	}

	public void shellSort(Comparable[] data) {

	}
	

}
