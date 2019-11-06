package com.techmania.spboot;

import java.io.Serializable;
import java.util.regex.Pattern;

public final class Employee implements Serializable {
    private final String name;

    public Employee(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        Employee that = (Employee) other;
        return this.name.equals(that.name);
    }

    public String getName() {
        return name;
    }

    public static boolean priceCheck(String input) {
        Pattern pattern = Pattern.compile("(?:[^a]*a[^a]*a)*");
        return pattern.matcher(input).matches();
    }

}
