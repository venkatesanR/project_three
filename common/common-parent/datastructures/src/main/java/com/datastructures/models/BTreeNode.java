package com.datastructures.models;


public class BTreeNode<Type> extends Node {
    private BTreeNode left;
    private BTreeNode right;

    public BTreeNode(Type data) {
        super(data);
    }

    public void setLeft(BTreeNode left) {
        this.left = left;
    }

    public void setRight(BTreeNode right) {
        this.right = right;
    }

    public BTreeNode getLeft() {
        return left;
    }

    public BTreeNode getRight() {
        return right;
    }
}
