package com.addval.validation;

import java.util.Locale;

public class RequestLocaleThreadLocal {
	private static ThreadLocal<Locale> currentLocale = new ThreadLocal<Locale>();

	public static void setLocale(Locale locale) {
		currentLocale.set(locale);
	}

	public static Locale getLocale() {
		return currentLocale.get();
	}

	public static void removeLocale() {
		currentLocale.remove();
	}

}
