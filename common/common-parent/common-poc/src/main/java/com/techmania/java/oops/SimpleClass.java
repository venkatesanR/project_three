package com.techmania.java.oops;

/**
 * Class ment to be blueprint of steps before to do any job
 * 
 * Accessibility/Visble: , 1.public 2.abstract-Need to subclass 3.final _-Cannot
 * be subclass 4.Abstract and final Mutually exclusive
 * 
 * @author venkat_raji
 *
 */
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

	//alternative for no-arg constructors
	
	{
		logMe = 13;

	}

	//static intializers(checked exceptions should not thrown)
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
