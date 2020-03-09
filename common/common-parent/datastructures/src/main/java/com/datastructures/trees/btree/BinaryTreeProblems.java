package com.datastructures.trees.btree;

import com.datastructures.models.BTreeNode;

import java.util.*;
import java.util.stream.Collectors;

public class BinaryTreeProblems {
    /**
     * Go to the  left ,Then right and
     * and compare with three
     *
     * @param node
     * @return
     */
    public Boolean isPresent(BTreeNode node, Object toBeFind) {
        if (node == null) {
            return Boolean.FALSE;
        }
        Queue<BTreeNode> bfs = new LinkedList<>();
        bfs.add(node);
        while (!bfs.isEmpty()) {
            BTreeNode dataNode = bfs.poll();
            if (Objects.equals(toBeFind, dataNode.getData())) {
                return Boolean.TRUE;
            }

            if (dataNode.getLeft() != null) {
                bfs.offer(dataNode.getLeft());
            }
            if (dataNode.getRight() != null) {
                bfs.offer(dataNode.getRight());
            }
        }
        return Boolean.FALSE;
    }

    public Boolean deepFind(BTreeNode node, Object toBeFind) {
        BTreeNode temp;
        if (node == null) {
            return Boolean.FALSE;
        } else {
            if (Objects.equals(toBeFind, node.getData())) {
                return Boolean.TRUE;
            } else {
                // temp = deepFind(node.getData(), toBeFind);
                return Boolean.TRUE;
            }
        }
    }

    /**
     * Go to the  left ,Then right and
     * and compare with three
     *
     * @param node
     * @return
     */
    public <T extends Comparable> T max(BTreeNode node,
                                        Class<T> clazz) {
        if (node == null) {
            return null;
        }
        T max = null;
        Queue<BTreeNode> bfs = new LinkedList<>();
        bfs.add(node);
        while (!bfs.isEmpty()) {
            BTreeNode<T> dataNode = bfs.poll();
            if (max == null ||
                    max.compareTo(dataNode.getData()) < 0) {
                max = clazz.cast(dataNode.getData());
            }

            if (dataNode.getLeft() != null) {
                bfs.offer(dataNode.getLeft());
            }
            if (dataNode.getRight() != null) {
                bfs.offer(dataNode.getRight());
            }
        }
        return max;
    }

    public long size(BTreeNode node) {
        if (node == null) {
            return 0l;
        }
        return size(node.getLeft()) + size(node.getRight()) + 1;
    }

    public int height(BTreeNode node) {
        if (node == null) {
            return 0;
        } else {
            int rightDepth = height(node.getLeft());
            int leftDepth = height(node.getLeft());
            if (rightDepth > leftDepth) {
                return rightDepth + 1;
            } else {
                return leftDepth + 1;
            }
        }
    }

    private static int sum(BTreeNode<Integer> root, Integer key) {
        if (root == null) {
            return 0;
        }
        Integer value = root.getData();
        if ((root.getLeft() != null
                && root.getLeft().getData().equals(key))
                || (root.getRight() != null
                && root.getRight().getData().equals(key))) {
            value = 0;
        } else if (value.equals(key)) {
            value -= root.getLeft() != null ? root.getLeft().getData() : 0;
            value -= root.getRight() != null ? root.getRight().getData() : 0;
        }
        System.out.println("Node: " + root.getData() + ", Sum: " + value);
        value += sum(root.getLeft(), key);
        value += sum(root.getRight(), key);
        return value;
    }

    /**
     * 10
     * 2     6
     * 1   5  9   8
     *
     * @param args
     */
    public static void main(String[] args) {
        BTreeNode<Integer> one = new BTreeNode<>(1);
        BTreeNode<Integer> five = new BTreeNode<>(5);
        BTreeNode<Integer> two = new BTreeNode<>(2);
        two.setLeft(one);
        two.setRight(five);

        BTreeNode<Integer> six = new BTreeNode<>(6);
        BTreeNode<Integer> nine = new BTreeNode<>(9);
        BTreeNode<Integer> eight = new BTreeNode<>(8);
        six.setLeft(nine);
        six.setRight(eight);

        BTreeNode<Integer> ten = new BTreeNode<>(10);
        ten.setLeft(two);
        ten.setRight(six);
        System.out.println(sum(ten, 2));
    }
}
