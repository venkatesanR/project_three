package com.techland.training.krishna;

import java.awt.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Fibonacci {

	static int userInput;
	static int limit = 5;
	static ArrayList<Integer> list;
	static ArrayList<Integer> finalList;

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

	public static ArrayList<Integer> sequence(ArrayList<Integer> list) {
		int startValue;
		int nextValue;
		if (list.contains(userInput)) {
			System.out.println("prime");
			startValue = userInput;
//			System.out.println(startValue);
			nextValue = list.get(list.size() - 2) + startValue;
//			System.out.println(nextValue);
		} else {
			System.out.println("Not a prime");
			startValue = list.get(list.size() - 1);
//			System.out.println(startValue);
			nextValue = list.get(list.size() - 2) + startValue;
//			System.out.println(nextValue);
		}

		ArrayList<Integer> finalList = new ArrayList<Integer>();
//		int count = 0;
//		finalList.add(startValue);
//		while (count < limit) {
//			finalList.add(nextValue);
//			nextValue = startValue + nextValue;
//			startValue = nextValue - startValue;
//		}
//		System.out.println(finalList);
		return finalList;
	}

	public static void main(String[] args) {
		userinput();
		find();
		System.out.println(sequence(find()));
	}
}
