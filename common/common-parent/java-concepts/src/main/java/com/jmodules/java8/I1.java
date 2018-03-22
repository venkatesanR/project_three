package com.jmodules.java8;

public interface I1 {
	String getName();

	default void print() {
		System.out.println("Hell I1");
	}
}
