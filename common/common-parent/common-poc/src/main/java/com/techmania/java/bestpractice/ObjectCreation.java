package com.techmania.java.bestpractice;

public class ObjectCreation {
	/**
	 * <p>
	 * #1.<b>Consider static factory instead of constructors This is not factory
	 * design pattern but its advisable instead of using public constructors and its
	 * readability()-->BigInteger(int, int,
	 * Random)<-->BigInteger.probablePrime();</b> <br>
	 * #2.<b>static factory methods is that, unlike constructors, they are not
	 * required to create a new object each time they’re invoked </b><br>
	 * 
	 * #3.<b>Use single constructor with unique signature If signature vary by order
	 * Then its bad idea</b>
	 *
	 * 
	 * #4.<b>Pattern : Flyweight pattern/<b>
	 * 
	 * Interface based implementation(Collection) Super and sub type based
	 * call(EnumSet,JumboSet..)
	 * 
	 * 
	 * </p>
	 **/
	public static Boolean getBoolean(boolean input) {
		return input ? Boolean.TRUE : Boolean.FALSE;
	}

	/****
	 * Service provider framework SKETCH 1.service interface <br>
	 * 2.service provider interface <br>
	 * 3.service registration//non instantiable<br>
	 * 4.service provider<br>
	 * 5.Service access<br>
	 ****/

	public static Service getService(String name) {
		return name != null ? Services.newInstance(name) : Services.newInstance();
	}

}
