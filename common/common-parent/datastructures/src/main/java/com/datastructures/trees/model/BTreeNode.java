package com.datastructures.trees.model;

public class BTreeNode<T> extends AbstractNode<T> {
    private static final String NODE_FORMAT = "{ %s } -> [%s,%s]";
    private BTreeNode left;
    private BTreeNode right;

    public BTreeNode(T data) {
        super(data);
    }

    public BTreeNode(T data, Boolean isRoot) {
        super(data, isRoot);
    }

    public BTreeNode getLeft() {
        return left;
    }

    public void setLeft(BTreeNode left) {
        this.left = left;
    }

    public BTreeNode getRight() {
        return right;
    }

    public void setRight(BTreeNode right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format(NODE_FORMAT, getData(), left != null ? left.getData() : "NULL", right != null ?
                right.getData() : "NULL");
    }
}
