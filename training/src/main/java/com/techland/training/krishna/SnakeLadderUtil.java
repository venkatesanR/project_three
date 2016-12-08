package com.techland.training.krishna;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SnakeLadderUtil {
	public static Map<Integer, Integer> ladder = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> slope = new HashMap<Integer, Integer>();
	public static Random random = new Random();
	static int max = 6;
	static int min = 1;

	private static void loadLadder() {
		ladder.put(4, 14);
		ladder.put(9, 31);
		ladder.put(20, 38);
		ladder.put(28, 84);
		ladder.put(40, 59);
		ladder.put(51, 67);
		ladder.put(63, 81);
		ladder.put(71, 91);
	}

	private static void loadSlopeData() {
		slope.put(17, 7);
		slope.put(54, 34);
		slope.put(62, 19);
		slope.put(64, 60);
		slope.put(87, 24);
		slope.put(93, 73);
		slope.put(95, 75);
		slope.put(99, 78);
	}

	public static Integer getLadder(Integer position) {
		if (ladder.isEmpty()) {
			loadLadder();
		}
		return ladder.get(position);
	}

	public static Integer getSlope(Integer position) {
		if (slope.isEmpty()) {
			loadSlopeData();
		}
		return slope.get(position);
	}

	public static int throwDice() {
		int randomValue = random.nextInt((max - min) + 1) + min;
		return randomValue;
	}
}
