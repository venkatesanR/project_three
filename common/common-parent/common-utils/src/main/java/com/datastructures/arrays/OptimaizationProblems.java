package com.datastructures.arrays;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class OptimaizationProblems {
	public static void main(String[] args) throws IOException {
		int[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		System.out.println(transmitterProblem(a, 2));
	}

	static int transmitterProblem(int[] a, int t) {
		int total = 0;
		int diff = 0;
		for (int i = 1; i < a.length - 1; i++) {
			diff = diff + a[i] - a[i - 1];
			if (diff >= 2 * t) {
				total += 1;
				i=i+t;
				diff=0;
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
