package com.datastructures.trees.model;

public class BinaryTree<T> {
    private BTreeNode<T> root;
    private BTreeNode<T> current;
    private boolean left = true;

    public BinaryTree(T data) {
        root = new BTreeNode<>(data, true);
        current = root;
    }

    private void add(T data) {
        if (current.getLeft() == null || current.getRight() == null) {
            if (current.getLeft() == null) {
                current.setLeft(new BTreeNode(data, false));
            } else if (current.getRight() == null) {
                current.setRight(new BTreeNode(data, false));
            }
        } else {
            if (left) {
                current = current.getLeft();
                current.setLeft(new BTreeNode(data, false));
                left = false;
            } else {
                current = current.getRight();
                current.setRight(new BTreeNode(data, false));
                left = true;
            }

        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        builder.append("\n");
        convertToString(builder, root);
        builder.append(']');
        return builder.toString();
    }

    private void convertToString(StringBuilder builder, BTreeNode node) {
        if (node == null) {
            return;
        }
        builder.append(node.toString());
        builder.append("\n");
        convertToString(builder, node.getLeft());
        convertToString(builder, node.getRight());
    }

    public static void main(String[] args) {
        BinaryTree<String> tree = new BinaryTree<>("A");
        tree.add("B");
        tree.add("C");
        tree.add("D");
        tree.add("E");
        tree.add("F");
        tree.add("G");
        tree.add("H");
        tree.add("I");
        tree.add("J");
        tree.add("K");
        System.out.println(tree.toString());
    }
}
