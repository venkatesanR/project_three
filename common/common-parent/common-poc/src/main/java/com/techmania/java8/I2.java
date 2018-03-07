package com.techmania.java8;

public interface I2 {
	String getName();
	default void print(){
		System.out.println("I2 logging::");
	}
}
