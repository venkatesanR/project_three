package com.datastructures.arrays;

import java.util.LinkedList;
import java.util.Queue;

public class ElevatorTask {
	private static final Queue<ElevatorInput> JOB = new LinkedList<>();

	public static void main(String[] args) {

	}

	public void operate() {

	}

	class ElevatorInput {
		int persons;
		int weight;

		public ElevatorInput(int p, int w) {
			this.weight = w;
			this.persons = p;
		}
	}
}
