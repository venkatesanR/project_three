package com.datastructures.string;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class HackerRankSoultion {
	public static String isSherLock(String s) {
		String response = "";
		Set<Integer> unique = new HashSet<>();
		char[] input = s.toCharArray();
		Map<String, Integer> chars = new HashMap<>();
		for (int i = 0; i < input.length; i++) {
			Integer op = chars.get(String.valueOf(input[i]));
			if (op == null) {
				op = 1;
			} else {
				op = op + 1;
			}
			chars.put(String.valueOf(input[i]), op);
		}
		for (Entry<String, Integer> entry : chars.entrySet()) {
			unique.add(entry.getValue());
		}
		if (unique.size() == 1) {
			response = "YES";
		} else if (unique.size() == 2) {
			Iterator<Integer> iter = unique.iterator();
			int m1 = iter.next();
			int m2 = iter.next();
			int abs = Math.abs(m2 - m1);
			if ((input.length - m1) % m2 == 0 && m1 == 1) {
				response = "YES";
			} else if ((input.length - m2) % m1 == 0 && m2 == 1) {
				response = "YES";
			} else if (abs == 1) { 
				int count = 0;
				for (Map.Entry<String, Integer> entry : chars.entrySet()) {
					if (Integer.valueOf(1).equals(entry.getValue())) {
						count += 1;
					}
				}
				if (count == 1 || count == 0) {
					response = "YES";
				} else {
					response = "NO";
				}
			} else {
				response = "NO";
			}
		} else {
			response = "NO";
		}
		return response;
	}

	public static void main(String[] args) {
		String data = "hfchdkkbfifgbgebfaahijchgeeeiagkadjfcbekbdaifchkjfejckbiiihegacfbchdihkgbkbddgaefhkdgccjejjaajgijdkd";
		System.out.println(isSherLock(data));
	}
}
