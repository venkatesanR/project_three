package com.techmania.designprinciples.assignments.services;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceHandler<T> implements InvocationHandler {
    private static final String EXECUTION_FORMAT = "Method [%s] executed in [%s] msec";
    private String name;
    private final T original;

    public ServiceHandler(String name, T original) {
        this.original = original;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object returnValue = null;
        try {
            long currentTime = System.nanoTime();
            returnValue = method.invoke(original, args);
            System.out.println(String.format(EXECUTION_FORMAT, method.getName(),
                    System.nanoTime() - currentTime / 1000));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return returnValue;
    }
}
