package com.datastructures.graphs;

import java.util.HashMap;
import java.util.Map;

public class DATA {
	private static Map<String, Double> DISTANCE_DETAILS = new HashMap<>();
	static {
		DISTANCE_DETAILS.put("01", 7.0);
		DISTANCE_DETAILS.put("02", 8.0);
		DISTANCE_DETAILS.put("03", 102.3);
		DISTANCE_DETAILS.put("14", 5.5);
		DISTANCE_DETAILS.put("45", 8.2);
		DISTANCE_DETAILS.put("26", 4.0);
		DISTANCE_DETAILS.put("27", 12.3);
		DISTANCE_DETAILS.put("79", 295.8);
		DISTANCE_DETAILS.put("78", 3.1);
		DISTANCE_DETAILS.put("59", 280.1);
		DISTANCE_DETAILS.put("39", 391.2); 		

	}

	enum GEO {
		VEERAGANUR(0, "VEERAGANUR"), KRISHNAPURAM(1, "KRISHNAPURAM"), GANGAVALLI(2, "GANGAVALLI"), NAMAKAL(3,
				"NAMAKAL"), VEPPANTHATTAI(4, "VEPPANTHATTAI"), NADUVALUR(6, "NADUVALUR"), ATTUR(7,
						"ATTUR"), AMMAPALAYAM(8, "AMMAPALAYAM"), PERAMBALUR(5, "PERAMBALUR"), CHENNAI(9, "CHENNAI");
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
