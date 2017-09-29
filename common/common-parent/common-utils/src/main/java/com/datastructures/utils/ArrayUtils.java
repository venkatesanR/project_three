package com.datastructures.utils;

public class ArrayUtils {
	public static <T> String print(int[] input, Character... charecter) {
		StringBuilder builder = new StringBuilder();
		for (int data : input) {
			builder.append(data).append((charecter != null && charecter.length > 0 ) ? charecter[0] : ',');
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}

	public static <T> String print(Object[] input, Character.Subset... charecter) {
		StringBuilder builder = new StringBuilder();
		for (Object data : input) {
			builder.append(data).append(charecter != null ? charecter[0] : ',');
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}
}
