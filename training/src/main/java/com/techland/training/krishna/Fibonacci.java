package com.techland.training.krishna;

import java.util.ArrayList;
import java.util.Scanner;

public class Fibonacci {

	static int userInput;
	static int limit = 5;
	static ArrayList<Integer> list;

	public static int userinput() {
		Scanner scan = new Scanner(System.in);
		userInput = scan.nextInt();
		scan.close();
		// System.out.println(userInput);
		return userInput;
	}

	public static ArrayList<Integer> find() {
		int startValue = 0;
		int nextValue = 1;

		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(startValue);
		while (nextValue <= userInput) {
			list.add(nextValue);
			nextValue = startValue + nextValue;
			startValue = nextValue - startValue;
		}
		// System.out.println(list);

		return list;
	}

	public static void sequence(ArrayList<Integer> list) {
		int startValue;
		int nextValue;

		if (list.contains(userInput)) {
			System.out.println("prime");
			startValue = userInput;
			nextValue = list.get(list.size() - 2) + startValue;
		} else {
			System.out.println("Not a prime");
			startValue = list.get(list.size() - 1) + list.get(list.size() - 2);
			nextValue = list.get(list.size() - 1) + startValue;
		}

		int count = 1;
		System.out.println(startValue);
		while (count < limit) {
			System.out.println(nextValue);
			nextValue = startValue + nextValue;
			startValue = nextValue - startValue;
			count++;
		}

	}

	public static void main(String[] args) {
		userinput();
		find();
		sequence(find());
	}
}
