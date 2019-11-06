package com.techmania.designprinciples.assignments.pircecalculator.cache;

public interface ICacheConstructor<K, V> {
    void init(String cacheName);

    void populate();

    void add(K key, V value);

    V get(K key);

    void clear();
}
