package com.techmania.designprinciples.assignments.pircecalculator.model;

import java.util.HashSet;
import java.util.Set;

public class PurchaseResponse {
    private Set<Product> products;
    private float total;
    private float saved;
    private float actual;

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public Set<Product> getProducts() {
        if (products == null) {
            this.products = new HashSet<>();
        }
        return products;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public void setSaved(float saved) {
        this.saved = saved;
    }

    public float getSaved() {
        return saved;
    }

    public float getActual() {
        return actual;
    }

    public void setActual(float actual) {
        this.actual = actual;
    }
}
