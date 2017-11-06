package com.datastructures.workouts;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class DummyWorkout {
	public static void main(String[] args) throws IOException {
		prepareConfigFiles(
				"/home/YUME.COM/vrengasamy/workspace/YFN/Yumeos/PacingService/src/main/java/com/yumecorp/pacingservice",
				"pacingservice");
	}
	
	private static void prepareConfigFiles(String baseLocation,String baseName) throws IOException {
		String[] javaExtn = new String[] { "java" };
		Collection<File> entityList = FileUtils.listFiles(new File(baseLocation + "/bean"), javaExtn, false);
		for (File file : entityList) {
			String serviceFileName = (baseLocation + "/bean").replaceAll("/", ".").concat(file.getName().replaceAll(".java", ""));
			System.out.println(serviceFileName);
		}
	}

	public static void recurse(int[] array, int pivot) {
		array[pivot] = 1;
		print(array);
		for (int i = pivot + 1; i < array.length; i++) {
			recurse(array, i);
			array[i] = 0;
		}
	}

	private static void print(int[] array) {
		StringBuffer buffer = new StringBuffer();
		for (int i : array) {
			buffer.append(i);
		}
		System.out.println(buffer.toString());
	}
}
