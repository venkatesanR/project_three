package com.datastructures.workouts;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class DummyWorkout {
	
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
	
	public static boolean checkEquals(String s,String r){
		s=s.intern();
		if(s == r){
			return true;
		}else{
			return false;
		}
	}
	public static void main(String[] args) throws IOException {
		int a=5,b=3,n=5;
		printSequence(a, b, n);
	}
	
	public static void printSequence(int a,int b,int n) {
		long sum=a;
		for(int i=0;i<n;i++) {
			sum+=getVal(a,b,i);
			System.out.println(sum);
		}
	}
	private static long getVal(int a,int b,int n) {
		long l=Double.valueOf(String.valueOf(Math.pow(2, n))).longValue();
		return (b*l);
	}
}
