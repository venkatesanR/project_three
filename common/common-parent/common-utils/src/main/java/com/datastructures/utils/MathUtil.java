package com.datastructures.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.InvalidArgumentException;

public class MathUtil {
	private MathUtil() {
		// NO
	}

	public static void main(String[] args) {
		String input = "2X+5-3X=7-X+3";
		long d=1000000000;
		System.out.println(solveSingleVarEquation(input, "X"));
	}
     
	private static String winner(String[] votes) {
		Map<String, Long> votesMap = new HashMap<String, Long>();
		for (String name : votes) {
			if(votesMap.get(name)!=null) {
				votesMap.put(name, votesMap.get(name)+1);
			} else {
				votesMap.put(name, Long.valueOf(1));
			}
		}
		List<Long> data=new ArrayList<>();
		votesMap.forEach((k,v)->{
			data.add(v);
		});
		Collections.sort(data,Collections.reverseOrder());
		List<String> names=new ArrayList<>();
		votesMap.forEach((k,v)->{
			if(v.equals(data.get(0))) {
				names.add(k);
			}
		});
		Collections.sort(names,Collections.reverseOrder());
		return names.get(0);

	}
	
	
	public static float solveSingleVarEquation(String input, String variableName) {
		String[] splited = input.split("[-+=]");
		float coEffVar = 0.0f;
		float constant = 0.0f;
		String modData;
		String sigNum = "";
		int signIndex = 0;
		for (int index = 0; index < splited.length; index++) {
			signIndex = 0;
			if (index - 1 > 0) {
				signIndex = index - 1;
			}
			sigNum = String.valueOf(input.charAt(signIndex) != '-' ? '+' : '-');
			if ((splited[index].contains(variableName) || splited[index].contains(variableName.toUpperCase()))) {
				if (index - 2 > 0) {
					signIndex = index - 2;
				}
				modData = splited[index].replace(variableName, "").trim();
				coEffVar += Float.valueOf(modData.isEmpty() ? sigNum.concat("1") : sigNum.concat(modData));
			} else {
				constant += Float.valueOf(sigNum.concat(splited[index]).trim());
			}
		}
		if (coEffVar == 0.0f) {
			throw new InvalidArgumentException("Cannot diveded by Zero Because co-efficient sum is zero");
		}
		return -(constant / coEffVar);
	}

}
