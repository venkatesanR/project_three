package com.datastructures.models;


public class BTreeNode<Type> extends Node<Type> {
    private BTreeNode<Type> left;
    private BTreeNode<Type> right;

    public BTreeNode(Type data) {
        super(data);
    }

    public void setLeft(BTreeNode left) {
        this.left = left;
    }

    public void setRight(BTreeNode right) {
        this.right = right;
    }

    public BTreeNode<Type> getLeft() {
        return left;
    }

    public BTreeNode<Type> getRight() {
        return right;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
