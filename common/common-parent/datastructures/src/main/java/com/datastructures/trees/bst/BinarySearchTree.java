package com.datastructures.trees.bst;

import com.datastructures.models.BTreeNode;

public class BinarySearchTree<DataType extends Comparable> {
    private BTreeNode<DataType> root;

    public void add(DataType data) {
        root = insertIntoTree(root, data);
    }

    private BTreeNode insertIntoTree(BTreeNode<DataType> root, DataType data) {
        if (root == null) {
            root = new BTreeNode<>(data);
            return root;
        }
        if (data.compareTo(root.getData()) == -1) {
            root.setLeft(insertIntoTree(root.getLeft(), data));
        } else if (data.compareTo(root.getData()) == 1) {
            root.setRight(insertIntoTree(root.getRight(), data));
        }
        return root;
    }

    public void print() {
        printInOrder(root);
    }

    private void printInOrder(BTreeNode<DataType> root) {
        if (root == null) {
            return;
        }
        printInOrder(root.getLeft());
        System.out.println(root.getData());
        printInOrder(root.getRight());
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        tree.add(50);
        tree.add(30);
        tree.add(20);
        tree.add(40);
        tree.add(70);
        tree.add(60);
        tree.add(80);

        // print inorder traversal of the BST
        tree.print();
    }
}

