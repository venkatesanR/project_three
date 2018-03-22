package com.techmania.common.util;

public class RuntimeUtils {
	public static int getAvailableProcessors() {
		return getCurrentRunTime().availableProcessors();
	}

	public static long getOccupiedMemory() {
		return getCurrentRunTime().totalMemory() - getCurrentRunTime().freeMemory();
	}

	private static Runtime getCurrentRunTime() {
		return Runtime.getRuntime();
	}
}
