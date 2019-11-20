package com.techmania.spboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("coffee")
public class CoffeeProperties {
    private String name = "costa cafe";
    private double price = 7.3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
