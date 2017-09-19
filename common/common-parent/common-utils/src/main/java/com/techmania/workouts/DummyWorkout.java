package com.techmania.workouts;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DummyWorkout {
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static void main(String[] args) {
		Date date = null;
		try {
			SimpleDateFormat e = new SimpleDateFormat(DATE_FORMAT);
			date = stringToDate(e.format(new Date()));
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		System.out.println(date);
	}

	private static Date stringToDate(String datestr) throws Exception {
		if (datestr == null) {
			return null;
		}
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		return formatter.parse(datestr);
	}
}
