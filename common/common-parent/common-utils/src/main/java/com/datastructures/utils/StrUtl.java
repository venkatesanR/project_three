
package com.datastructures.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author ravi
 */
public class StrUtl {

	/**
	 * Creates a new instance of StrUtl
	 * 
	 * @roseuid 3EF36CDA014D
	 */
	public StrUtl() {

	}

	/**
	 * @param val
	 * @return boolean
	 * @roseuid 3EF36CDA0161
	 */
	public static boolean isEmpty(String val) {
		if (val == null || val.length() == 0)
			return true;
		return false;
	}

	/**
	 * @param val
	 * @return boolean
	 * @roseuid 3EF36CDA01A8
	 */
	public static boolean isEmptyTrimmed(String val) {
		if (val == null || val.trim().length() == 0)
			return true;
		return false;
	}

	/**
	 * compares two strings including nulls and returns true if they are the
	 * same including the case
	 *
	 * @param str1
	 * @param str2
	 * @return boolean
	 * @roseuid 3EF36CDA01EE
	 */
	public static boolean equals(String str1, String str2) {
		return (str1 == null) ? (str2 == null) : str1.equals(str2);
	}

	/**
	 * compares two strings including nulls and returns true if they are the
	 * same ignoring the case
	 *
	 * @param str1
	 * @param str2
	 * @return boolean
	 * @roseuid 3EF36CDA025C
	 */
	public static boolean equalsIgnoreCase(String str1, String str2) {
		return (str1 == null) ? (str2 == null) : str1.equalsIgnoreCase(str2);
	}

	public static String zeroPad(String srcstr, int width) {
		StringBuffer result = new StringBuffer("");

		for (int i = 0; i < width - srcstr.length(); i++)
			result.append("0");

		result.append(srcstr);
		return result.toString();
	}

	public static String padLeft(String val, String delimiter, int length) {
		if (val == null)
			return val;
		while (val.length() < length) {
			val = delimiter + val;
		}
		return val;
	}

	public static String padRight(String val, String delimiter, int length) {
		if (val == null)
			return val;
		while (val.length() < length) {
			val = val + delimiter;
		}
		return val;
	}

	public static String nvl(String val, String defaultVal) {

		if (val == null)
			return defaultVal;
		else
			return val;
	}

	public static String toProperCase(String val) {
		if (val == null) {
			return val;
		}

		if (val.length() == 1) {
			return val.toUpperCase();
		} else {
			val = val.substring(0, 1).toUpperCase() + val.substring(1).toLowerCase();
			return val;
		}
	}

	/**
	 * Returns a list of tokens (trimmed) derived from the input string. Uses
	 * separators as delimiting characters but separators are not returned as
	 * tokens. If separators is null or empty then , is used as a delimiter
	 * Could return an empty list but never a null
	 * 
	 * @param String
	 *            val - String to be tokenized
	 * @param String
	 *            separators - delimiting characters
	 * @return List - list of tokens
	 */
	public static List tokenize(String val, String separators) {
		ArrayList list = new ArrayList();

		if (StrUtl.isEmpty(val))
			return list;
		if (StrUtl.isEmpty(separators))
			separators = ",";

		StringTokenizer tokenizer = new StringTokenizer(val, separators);

		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken().trim());
		}

		return list;
	}

	/**
	 * Truncates the string to the length specified if longer
	 * 
	 * @param val
	 *            String to truncate
	 * @param length
	 *            max string length of the truncated string. Must >= 0. If < 0
	 *            considered as 0
	 */
	public static String truncate(String val, int length) {
		if (length < 0)
			length = 0;

		return val == null ? null : val.substring(0, val.length() > length ? length : val.length());
	}

	/**
	 * Method used to join the String with the given delimiter
	 * 
	 * @param inputs
	 * @param delimeter
	 * @return
	 */
	public static String join(Collection<String> inputs, String delimeter) {
		StringBuilder returnStringBuilder = new StringBuilder();
		if (!ListUtl.isEmpty(inputs)) {
			for (String inputStr : inputs) {
				returnStringBuilder.append(inputStr).append(delimeter);
			}
			returnStringBuilder.deleteCharAt(returnStringBuilder.length() - 1);
		}
		return returnStringBuilder.toString();
	}
}
