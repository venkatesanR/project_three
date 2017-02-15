package com.techland.training.krishna;

public class RangeLogic {

	public static void main(String[] args) {

// To find the value lies in which range		
		// int input = 0;
		//
		// int range1 = 80;
		// int range2 = 60;
		// int range3 = 40;
		// int range4 = 0;
		//
		// if (input < range3 && input != range4) {
		// System.out.println("40,0");
		// } else if (input < range2 && input > range3) {
		// System.out.println("60,40");
		// } else if (input < range1 && input > range2) {
		// System.out.println("80,60");
		// } else if (input >= 80) {
		// System.out.println("80");
		// } else if (input == range2) {
		// System.out.println("60");
		// } else if (input == range3) {
		// System.out.println("40");
		// } else {
		// System.out.println("0");
		// }
		//


// Login to convert the lower case to upper case characters and vice versa.
		String phrase = "I am Woriking at YUME";
		for (int i = 0; i < phrase.length(); i++) {
			if (!Character.isLowerCase(phrase.charAt(i))) {
				System.out.print(Character.toLowerCase(phrase.charAt(i)));
			} else {
				System.out.print(Character.toUpperCase(phrase.charAt(i)));
			}

		}

	}

}
