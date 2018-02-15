package com.datastructures.workouts;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DummyWorkout {

	private static void prepareConfigFiles(String baseLocation, String baseName) throws IOException {
		Map<Person, String> m1 = new HashMap<>();
		Person p1 = new Person(1, "venkat");
		m1.put(p1, p1.name);
		System.out.println(p1.hashCode());
		p1.name = "ss";
		System.out.println(p1.hashCode());
		System.out.println(m1.get(p1));
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

	public static boolean checkEquals(String s, String r) {
		s = s.intern();
		if (s == r) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		Map<Person, String> m1 = new HashMap<>();
		Person p1 = new Person(1, "venkat");
		m1.put(p1, p1.name);
		System.out.println(p1.hashCode());
		p1.name = "ss";
		System.out.println(m1.size());
		System.out.println(p1.hashCode());
		System.out.println(m1.get(p1));
	}

	public static void printSequence(int a, int b, int n) {
		long sum = a;
		for (int i = 0; i < n; i++) {
			sum += getVal(a, b, i);
			System.out.println(sum);
		}
	}

	private static long getVal(int a, int b, int n) {
		long l = Double.valueOf(String.valueOf(Math.pow(2, n))).longValue();
		return (b * l);
	}
}

class Person {
	int id;
	String name;

	public Person(int id, String name) {
		this.id = id;
		this.name = name;

	}
}
