package com.datastructures.arrays.problems;

import java.util.Arrays;
import java.util.List;

public class Solution {
	private static final List<String> VOWELS = Arrays
			.asList(new String[] { "a", "e", "i", "o", "u", "A", "E", "I", "O", "U" });
	private static final String APPENDER = "ay";

	public static void main(String[] args) {
		// Ellohay, eatay applesay?!?
		String data = "Hello, eat apples?!?";
		String[] englishWords = data.split(" ");
		System.out.println(pigLatin("Mayank"));
		Object[] dataO=new Object[1];
		dataO[0]=new Solution();
		System.out.println(Arrays.deepToString(englishWords));
	}

	private static String pigLatin(String input) {
		int firstOccurance = 0;
		String output = null;
		char firstVowel = 0;
		char[] chars = input.toCharArray();
		int splCharsIndex = containSpecialChar(chars);
		boolean isFirstCharUpper = String.valueOf(chars[0]).equals(String.valueOf(chars[0]).toUpperCase());
		for (char vowel : chars) {
			// finding vowel first occurrence index if yes break
			if (VOWELS.contains(String.valueOf(vowel))) {
				firstVowel = vowel;
				break;
			}
			firstOccurance += 1;
		}
		String forward = input.substring(0, firstOccurance);
		String specialStr = null;
		int modlength = input.length();
		if (splCharsIndex != -1) {
			modlength = splCharsIndex;
			specialStr = input.substring(splCharsIndex);
		}
		String backward = input.substring(firstOccurance, modlength);
		if (isFirstCharUpper) {
			backward = backward.replaceFirst(String.valueOf(firstVowel), String.valueOf(firstVowel).toUpperCase());
			forward = forward.replaceFirst(String.valueOf(chars[0]), String.valueOf(chars[0]).toLowerCase());
		}
		output = backward.concat(forward);
		if (specialStr != null) {
			output = output.concat(APPENDER).concat(specialStr);
		} else {
			output = output.concat(APPENDER);
		}
		return output != null ? output : input;
	}

	private static int containSpecialChar(char[] chars) {
		// "[a-zA-Z] to match only Alphabets"
		for (int i = 0; i < chars.length; i++) {
			if (!String.valueOf(chars[i]).matches("[a-zA-Z]")) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * n=4
	 *     #
	 *    ##
	 *   ###
	 *  ####
	 * @param n
	 */
	public static void stairCase(int n,int k) {
		if(k<=0) {
			return;
		}
		int i = 1;
		StringBuilder builder = new StringBuilder();
		while (i <= n) {
			String chars = (i < k) ? " " : "#";
			builder.append(chars);
			i++;
		}
		System.out.println(builder.toString());
		stairCase(n, k-1);
	}
	
	
}
