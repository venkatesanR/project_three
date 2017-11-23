package com.datastructures.arrays;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class OptimaizationProblems {
	public static void main(String[] args) throws IOException {
		String eqn = "3X+2-4=23";
		System.out.println(solveSingleVariableEqn(eqn, "X"));
	}

	static double solveSingleVariableEqn(String equation, String variable) {
		String[] sign = equation.split("[0-9,"+variable+",=]");
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
