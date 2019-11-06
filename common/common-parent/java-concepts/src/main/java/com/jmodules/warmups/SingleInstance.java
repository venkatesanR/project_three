package com.jmodules.warmups;

public class SingleInstance {

    // Type-1(Final-Public)
    private static final SingleInstance INSTANCE = new SingleInstance();

    private SingleInstance() {
        // No invocation or invoked ony
        // once
        // For privillaged reflective access Accesible object.setAccessible=true
        // (To make it defensive throw an exception on second instantion)
        throw new AssertionError("Please dont invoke this class");
    }

    // Static factory
    public static SingleInstance getInstance() {
        return INSTANCE;
    }

    // enum based invocation

}
