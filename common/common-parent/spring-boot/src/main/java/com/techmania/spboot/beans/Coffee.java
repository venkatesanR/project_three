package com.techmania.spboot.beans;

public class Coffee {
    private final String name;
    private final double price;
    private final long time;

    public Coffee(String name, double price, long time) {
        this.name = name;
        this.price = price;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("{%s,%s,%s}", name, price, time);
    }
}
