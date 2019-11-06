package com.techmania.designprinciples.assignments.pircecalculator.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DiscountCache implements ICacheConstructor<String, Float> {
    private static final Map<String, Float> PRODUCTS = new ConcurrentHashMap<>();
    private Object LOCK = new Object();
    private String cacheName;

    @Override
    public void add(String key, Float value) {
        PRODUCTS.put(key, value);
    }

    @Override
    public Float get(String key) {
        return PRODUCTS.get(key);
    }

    @Override
    public void init(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void clear() {
        synchronized (LOCK) {
            PRODUCTS.clear();
        }
    }

    public void populate() {
        //Load data from Source
    }
}
