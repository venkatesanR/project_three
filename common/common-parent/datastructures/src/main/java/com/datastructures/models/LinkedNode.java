package com.datastructures.models;


public class LinkedNode<Type> extends Node {
    private LinkedNode next;
    private LinkedNode prev;

    public LinkedNode(Type data, LinkedNode next) {
        super(data);
        this.next = next;
    }

    public LinkedNode(Type data, LinkedNode next, LinkedNode prev) {
        super(data);
        this.next = next;
        this.prev = prev;
    }

    public LinkedNode getNext() {
        return next;
    }

    public void setNext(LinkedNode next) {
        this.next = next;
    }

    public LinkedNode getPrev() {
        return prev;
    }

    public void setPrev(LinkedNode prev) {
        this.prev = prev;
    }
}
