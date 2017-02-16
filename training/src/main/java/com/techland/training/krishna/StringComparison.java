package com.techland.training.krishna;

import java.util.HashMap;
import java.util.Scanner;

public class StringComparison {
	static Scanner scan = new Scanner(System.in);
	static HashMap<Character, Integer> hm = new HashMap<Character, Integer>();
	static String word;

	public static void word() {
		System.out.println("Enter the word : ");
		word = scan.nextLine();
	}

	public static void frequency() {
		char tempCharcter;
		for (int i = 0; i < word.length(); i++) {
			tempCharcter = word.charAt(i);
			if (hm.containsKey(tempCharcter)) {
				hm.replace(tempCharcter, hm.get(tempCharcter) + 1);
			} else {
				hm.put(tempCharcter, 1);
			}
		}
	}

	public static void main(String[] args) {
		word();
		frequency();
		scan.close();
		System.out.println(hm);
	}

}
