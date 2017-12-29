package com.datastructures.graphs;

import java.util.HashMap;
import java.util.Map;

public class DATA {
	private static Map<String, Double> DISTANCE_DETAILS = new HashMap<>();
	static {
		DISTANCE_DETAILS.put("01", 12.1);
		DISTANCE_DETAILS.put("12", 13.1);
		DISTANCE_DETAILS.put("03", 13.7);
		DISTANCE_DETAILS.put("34", 19.1);
		DISTANCE_DETAILS.put("45", 276.6);
		DISTANCE_DETAILS.put("25", 290.8);
		DISTANCE_DETAILS.put("56", 7.3);
		DISTANCE_DETAILS.put("67", 10.3);
		DISTANCE_DETAILS.put("57", 17.6);

	}

	enum GEO {
		VEERAGANUR(0, "VEERAGANUR"), KRISHNAPURAM(1, "KRISHNAPURAM"), GANGAVALLI(2, "GANGAVALLI"), NAMAKAL(3,
				"NAMAKAL"), VEPPANTHATTAI(4, "VEPPANTHATTAI"), NADUVALUR(5, "NADUVALUR"), ATTUR(6,
						"ATTUR"), AMMAPALAYAM(7, "AMMAPALAYAM"), PERAMBALUR(8, "PERAMBALUR"), CHENNAI(9, "CHENNAI");
		private int id;
		private String name;

		GEO(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public static String getNameById(int i) {
			for (GEO g : GEO.values()) {
				if (i == g.id) {
					return g.name;
				}
			}
			return null;
		}
	}

	public static Double getDistance(int a, int b) {
		StringBuilder comb = new StringBuilder(String.valueOf(a) + String.valueOf(b));
		if (DISTANCE_DETAILS.get(comb.toString()) != null) {
			return DISTANCE_DETAILS.get(comb.toString());
		} else if (DISTANCE_DETAILS.get(comb.reverse().toString()) != null) {
			return DISTANCE_DETAILS.get(comb.reverse().toString());
		}
		return null;
	}
}
