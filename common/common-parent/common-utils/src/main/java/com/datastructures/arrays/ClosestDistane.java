package com.datastructures.arrays;

import java.util.HashMap;
import java.util.Map;

public class ClosestDistane {
	private static final String WHITE = "White";
	private static final String BLACK = "Black";
	private static final String RED = "Red";
	private static final String GREEN = "Green";
	private static final String BLUE = "Blue";

	public static void main(String[] args) {
		String[] pixels = { "101111010110011011100100", "110000010101011111101111", "100110101100111111101101",
				"010111011010010110000011", "000000001111111111111111" };
		String[] response = new String[pixels.length];
		int index = 0;
		for (String input : pixels) {
			String color = "";
			int r1 = Integer.valueOf(input.substring(0, 8), 2);
			int g1 = Integer.valueOf(input.substring(8, 16), 2);
			int b1 = Integer.valueOf(input.substring(16, input.length()), 2);
			Map<String, Double> result = new HashMap<String, Double>();
			result.put(WHITE, distance(r1, g1, b1, WHITE));
			result.put(RED, distance(r1, g1, b1, RED));
			result.put(GREEN, distance(r1, g1, b1, GREEN));
			result.put(BLUE, distance(r1, g1, b1, BLUE));
			result.put(BLACK, distance(r1, g1, b1, BLACK));
			Double distance = Double.MAX_VALUE;
			for (Map.Entry<String, Double> entry : result.entrySet()) {
				if (entry.getValue().doubleValue() < distance) {
					distance = entry.getValue().doubleValue();
					color = entry.getKey();
				} else if (distance == entry.getValue().doubleValue()) {
					color = "Ambiguous";
					break;
				}
			}
			response[index] = color;
			index += 1;
		}
		for (int i = 0; i < response.length; i++) {
			System.out.println(response[i]);
		}
	}

	private static double distance(int r2, int g2, int b2, String color) {
		int r1 = 0, g1 = 0, b1 = 0;
		if (color.equals(WHITE)) {
			r1 = 255;
			g1 = 255;
			b1 = 255;

		}
		if (color.equals(RED)) {
			r1 = 255;
			g1 = 0;
			b1 = 0;
		}
		if (color.equals(GREEN)) {
			r1 = 0;
			g1 = 255;
			b1 = 0;
		}
		if (color.equals(BLUE)) {
			r1 = 0;
			g1 = 0;
			b1 = 255;
		}
		int r = getDiffSquare(r1, r2);
		int g = getDiffSquare(g1, g2);
		int b = getDiffSquare(b1, b2);
		return Math.sqrt(r + g + b);

	}

	private static int getDiffSquare(int r1, int r2) {
		return (r1 - r2) * (r1 - r2);
	}
}
