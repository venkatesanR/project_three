package com.techmania.spboot.beans;

public class Coffee {
    private final String name;
    private final double price;

    public Coffee(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("{%s,%s}", name, price);
    }
}
