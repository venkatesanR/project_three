package com.techmania.spboot;

public final class Employee {
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

    public static void main(String[] args) {
        Employee e1 = new Employee("Hello");
        Employee e2 = new Employee("Hello");
        System.out.println(e1.equals(e2));
    }
}
