package com.techland.training.krishna;

import java.util.HashMap;
import java.util.Random;

public class SnakeAndLadder {

	static int currentposition = 0;

	public static int random() {
		Random random = new Random();
		int max = 6;
		int min = 1;
		int randomValue = random.nextInt((max - min) + 1) + min;
		System.out.println("strike : " + randomValue);
		return (randomValue);
	}

	public static HashMap<Integer, Integer> ladder() {
		HashMap<Integer, Integer> ladder = new HashMap<Integer, Integer>();
		ladder.put(4, 14);
		ladder.put(9, 31);
		ladder.put(20, 38);
		ladder.put(28, 84);
		ladder.put(40, 59);
		ladder.put(51, 67);
		ladder.put(63, 81);
		ladder.put(71, 91);

		return ladder;
	}

	public static HashMap<Integer, Integer> slope() {
		HashMap<Integer, Integer> slope = new HashMap<Integer, Integer>();
		slope.put(17, 7);
		slope.put(54, 34);
		slope.put(62, 19);
		slope.put(64, 60);
		slope.put(87, 24);
		slope.put(93, 73);
		slope.put(95, 75);
		slope.put(99, 78);

		return slope;
	}

	public static int position(int currentPosition) {
		
		int tempPosition = random() + currentPosition;
		
		if (ladder().containsKey(tempPosition)) {
			currentposition = ladder().get(tempPosition);
		} else if (slope().containsKey(tempPosition)) {
			currentposition = slope().get(tempPosition);
		} else if (tempPosition > 100){
			currentposition = currentPosition;
		} else if (tempPosition == 100){
			currentposition = 100;
		} else {
			currentposition = tempPosition;
		}
			

		return currentposition;
	}

	public static void main(String[] args) {

		while (currentposition != 100) {
			System.out.println("position : " + currentposition + " --> " + position(currentposition));
		}
		System.out.println("Game Over");

	}

}
