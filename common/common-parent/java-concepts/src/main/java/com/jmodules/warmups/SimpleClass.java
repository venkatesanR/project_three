package com.jmodules.warmups;

final class SimpleClass {

	// Class members

	// Attributes
	// annotations
	// access modifiers(public(C),private,protected(NC,M))
	// static
	// final
	// transient,volatile
	private int legth = 10;

	// class variable
	private static int invocationCount = 0;

	int logMe = 12;
	private final double PI = 3.14;

	private final long data;

	// alternative for noarg constructors

	{
		logMe = 13;
	}

	// static intializers(checked exceptions should not thrown)
	static {
		invocationCount += 1;
	}

	public SimpleClass() {
		data = 1;
	}

	// Methods
	public int getLenght() {
		return legth;
	}

	class NestedMemeber {
		int delay2;
	}
}
