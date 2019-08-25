package com.datastructures.arrays;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MovieNight {
	public static String firstUniqueName(String[] names) {
		Map<String, Integer> occurances = new HashMap<>();
		for (int i = 0; i < names.length; i++) {
			if (occurances.get(names[i]) == null) {
				occurances.put(names[i], 1);
			} else {
				occurances.put(names[i], occurances.get(names[i]) + 1);
			}
		}
		for (int i = 0; i < names.length; i++) {
			if (occurances.get(names[i]).intValue() == 1) {
				return names[i];
			}
		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println(firstUniqueName(new String[] { "Abbi", "Adeline", "Abbi", "Adalia" }));
	}
}

class Movie {
	private Date start, end;

	public Movie(Date start, Date end) {
		this.start = start;
		this.end = end;
	}

	public Date getStart() {
		return this.start;
	}

	public Date getEnd() {
		return this.end;
	}
}