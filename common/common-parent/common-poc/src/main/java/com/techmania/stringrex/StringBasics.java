package com.techmania.stringrex;

public class StringBasics {
	// creation
	private static String data = "test";

	public void construct() {
		data = new String();// EmptyString
		data = new String("copy");// uses copy constructor
		data = new String(new char[] { 'a' });// char array
		data = new String(new StringBuilder());// char array
	}

	private void indexOperations(String str, String ch, int start) {
		data.indexOf(ch); // first position of ch
		data.indexOf(ch, start);
		data.indexOf(str);
		data.indexOf(str, start);
		data.lastIndexOf(ch);
		data.lastIndexOf(ch, start);
		data.lastIndexOf(str);
		data.lastIndexOf(str, start);
	}

}
