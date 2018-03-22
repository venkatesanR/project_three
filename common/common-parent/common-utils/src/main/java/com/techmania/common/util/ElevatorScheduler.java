package com.techmania.common.util;

import java.util.LinkedList;
import java.util.Queue;


 class ElevatorScheduler implements Runnable {
	public Queue<Integer> persons;
	private int maxPersons;
	private int maxWeight;

	public ElevatorScheduler(int maxPersons, int maxWeight) {
		this.maxPersons = maxPersons;
		this.maxWeight = maxWeight;
	}

	public boolean isMaxWeightReached(Elevator elevator) {
		return elevator.getCurrentWeight() > maxWeight;
	}

	public boolean isMaxPersonsReached(Elevator elevator) {
		return elevator.getPersonInside() > maxPersons;
	}

	private void populatePersons(int[] weights) {
		persons = new LinkedList<>();
		for (int i = weights.length - 1; i >= 0; i--) {
			persons.add(weights[i]);
		}
	}

	@Override
	public void run() {
		while (!persons.isEmpty()) {
			if (persons.isEmpty()) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class Elevator {
	private int id;
	private int currentWeight;
	private int personInside;
	private int totalStops;

	public int getId() {
		return id;
	}

	public int getTotalStops() {
		return totalStops;
	}

	public void setTotalStops(int totalStops) {
		this.totalStops = totalStops;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(int currentWeight) {
		this.currentWeight = currentWeight;
	}

	public int getPersonInside() {
		return personInside;
	}

	public void setPersonInside(int personInside) {
		this.personInside = personInside;
	}

}
