package com.datastructures.arrays;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class RangeQuerys {
	private static DecimalFormat format = new DecimalFormat("##.00");

	enum COLURS {
		WHITE("White"), BLACK("Black"), RED("Red"), GREEN("Green"), BLUE("Blue");
		private String desc;

		COLURS(String desc) {
			this.desc = desc;
		}
	}

	private static final String AMBIGIOUS = "Ambiguous";
	private static final Map<Double, String> combinations = new HashMap<>();

	public static void main(String[] args) {
		String data = "000000001111110001111000";

	}

	public static String closestPixel(String data) {
		int r2 = Integer.valueOf(data.substring(0, 8), 2);
		int g2 = Integer.valueOf(data.substring(8, 16), 2);
		int b2 = Integer.valueOf(data.substring(16, data.length()), 2);
		for (COLURS col : COLURS.values()) {
			combinations.put(distance(r2, g2, b2, col.desc), col.desc);
		}
		Double dmin = Double.MAX_VALUE;
		String name = "";
		for (Map.Entry<Double, String> entry : combinations.entrySet()) {
			if (dmin != null && entry.getKey().doubleValue() < dmin.doubleValue()) {
				dmin = entry.getKey().doubleValue();
				name = entry.getValue();
			} else if (dmin != null && dmin.doubleValue() == entry.getKey().doubleValue()) {
				System.out.println(AMBIGIOUS);
				break;
			}
		}
		return name;
	}

	private static double distance(int r2, int g2, int b2, String color) {
		int r1 = 0, g1 = 0, b1 = 0;
		if (COLURS.RED.desc.equals(color)) {
			r1 = 255;
		} else if (COLURS.GREEN.desc.equals(color)) {
			g1 = 255;
		} else if (COLURS.BLUE.desc.equals(color)) {
			b1 = 255;
		} else if (COLURS.WHITE.desc.equals(color)) {
			r1 = 255;
			g1 = 255;
			b1 = 255;
		}
		int rdiff = (r1 - r2);
		int gdiff = g1 - g2;
		int bdiff = b1 - b2;

		return Double.valueOf(format.format(Math.sqrt((rdiff * rdiff) + (gdiff * gdiff) + (bdiff * bdiff))));
	}
}
