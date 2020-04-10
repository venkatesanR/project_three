package com.techmania.spboot.javafiles;

public interface CustomList<T> {

    public void add(T element);

    public T get(int index);

    public String values();

    public void remove(int index);
}
