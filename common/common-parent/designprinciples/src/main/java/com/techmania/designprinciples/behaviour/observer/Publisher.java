package com.techmania.designprinciples.behaviour.observer;

public interface Publisher {
    public void subScribe(Listener listener);

    public void unSubScribe(Listener listener);

    public void notifyUpdate();
}
