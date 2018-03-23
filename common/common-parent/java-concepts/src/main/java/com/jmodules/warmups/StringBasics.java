package com.jmodules.warmups;

public class StringBasics {
	// creation
	private static String data = "Hi i am a cool robot";
	private static String[] arrs=new String[]{"A","B","C"};
	public void construct() {
		data = new String();// EmptyString
		data = new String("copy");// uses copy constructor
		data = new String(new char[] { 'a' });// char array
		data = new String(new StringBuilder());// char array
	}

	public void indexOperations(String str, String ch, int start) {
		data.indexOf(ch); // first position of ch
		data.indexOf(ch, start);
		data.indexOf(str);
		data.indexOf(str, start);
		data.lastIndexOf(ch);
		data.lastIndexOf(ch, start);
		data.lastIndexOf(str);
		data.lastIndexOf(str, start);
	}
	
	public static void main(String[] args) {
		System.out.println(reagionMatches(data, "Robot", true));
	}
	
	public static boolean reagionMatches(String from,String to,boolean ignoreCase) {
		return from.regionMatches(ignoreCase,to.length(), to, from.length(), from.length());
	}
	//Binary search in a array
	public int positiong(String key) {
		int low = 0;
		int high = arrs.length - 1;
		while (low <= high) {
			int mid = ((high - low) / 2) + 1;
			int cmp = key.compareTo(arrs[mid]);
			if (cmp == 0) {
				return low;
			} else if (cmp < 0) {
				high = mid - 1;
			} else {
				low = mid + 1;
			}
		}
		return -1;
	}

}
