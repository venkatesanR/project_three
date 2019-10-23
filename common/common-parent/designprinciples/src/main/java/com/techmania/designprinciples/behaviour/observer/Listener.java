package com.techmania.designprinciples.behaviour.observer;

public interface Listener<T> {
    public void onUpdate(T data);
}
