package com.datastructures.arrays;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class OptimaizationProblems {
	public static void main(String[] args) throws IOException {
		int[] a = { 7, 2 ,4 ,6 ,5 ,9 ,12 ,11  };
		System.out.println(transmitterProblem(a, 2));
	}

	/**
	 * This approach is a brute force approach to predict
	 * 
	 * @param a
	 * @param t
	 * @return
	 */
	static int transmitterProblem(int[] a, int t) {
		Arrays.sort(a);
		int total = 0;
		int diff = 0;
		int i = 1;
		while (i < a.length) {
			diff = diff + (a[i] - a[i - 1]);
			if (diff > t) {
				total += 1;
				int j = i;
				diff = 0;
				while (j+1 < a.length && diff < t) {
					diff = diff + a[j + 1] - a[j];
					j++;
				}
				i = j;
				diff = 0;
			} else {
				i = i + 1;
			}

		}
		return total;
	}

	static double solveSingleVariableEqn(String equation, String variable) {
		String[] sign = equation.split("[0-9," + variable + ",=]");
		String[] variables = equation.split("[-?+=]");
		double varCoEff = 0;
		double constant = 0;
		boolean foundEqual = false;
		for (int index = 0; index < variables.length; index++) {
			String appender = "";
			if (sign[index] != null) {
				if (!foundEqual && (sign[index].trim() == "+" || sign[index].trim() == "")) {
					appender = "+";
				} else if (!foundEqual && (sign[index].trim() == "-")) {
					appender = "-";
				}
				if (foundEqual && (sign[index].trim() == "+" || sign[index].trim() == "")) {
					appender = "-";
				} else if (foundEqual && (sign[index].trim() == "-")) {
					appender = "+";
				} else {
					foundEqual = true;
					continue;
				}
				if (variables[index].contains(variable)) {
					varCoEff = varCoEff + Double.valueOf(appender.concat(variable.replace(variable, "")));
				} else {
					constant = constant + Double.valueOf(appender.concat(variable));
				}
			}

		}
		return (varCoEff > 0 ? (-constant / varCoEff) : 0.0);
	}

	static String isBalanced(String s) {
		// Complete this function
		if (s != null && !s.trim().equals("")) {
			String matched = "YES";
			if (s.length() % 2 != 0) {
				matched = "NO";
				return matched;
			}
			List<String> left = Arrays.asList(new String[] { "{", "[", "(" });
			List<String> right = Arrays.asList(new String[] { "}", "]", ")" });
			Stack<String> ls = new Stack<String>();
			for (char c : s.toCharArray()) {
				String modC = String.valueOf(c);
				if (left.contains(modC)) {
					ls.push(modC);
				} else if (right.contains(modC)) {
					if (!ls.isEmpty()) {
						String modl = ls.pop();
						if (left.indexOf(modl) != right.indexOf(modC)) {
							matched = "NO";
							break;
						}
					} else {
						matched = "NO";
						break;
					}
				}
			}
			if (!ls.isEmpty()) {
				matched = "NO";
			}
			return matched;

		} else {
			return "";
		}
	}

}
