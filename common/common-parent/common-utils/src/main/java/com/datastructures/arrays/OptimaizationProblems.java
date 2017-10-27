package com.datastructures.arrays;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class OptimaizationProblems {
	public static void main(String[] args) throws IOException {
		String data="[()][{}[{}[{}]]][]{}[]{}[]{{}({}(){({{}{}[([[]][[]])()]})({}{{}})})}";
		/*List<String> resp = FileUtils.readLines(new File("/home/YUME.COM/vrengasamy/Desktop/input.txt"));
		for (int index = 1; index < resp.size(); index++) {
			System.out.println(isBalanced(resp.get(index)));
		}*/
		System.out.println(isBalanced(data));
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
				} else if (right.contains(modC) && !ls.isEmpty()) {
					String modl = ls.pop();
					if (left.indexOf(modl) != right.indexOf(modC)) {
						matched = "NO";
						break;
					}
				}
			}
			return matched;

		} else {
			return "";
		}
	}

}
