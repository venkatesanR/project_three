package com.datastructures.models;

public class Node<Type> {
    private Type data;

    public Node(Type data) {
        this.data = data;
    }

    public Type getData() {
        return data;
    }

    public void setData(Type data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "[ " + data.toString() + " ]";
    }
}
