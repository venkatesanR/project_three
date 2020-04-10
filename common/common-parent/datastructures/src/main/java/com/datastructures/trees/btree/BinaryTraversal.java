package com.datastructures.trees.btree;

import com.datastructures.models.BTreeNode;
import com.datastructures.models.Node;
import com.datastructures.trees.model.TraverseType;
import com.techmania.common.exceptions.InvalidOperationException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class BinaryTraversal implements ITraversalFactory {

    @Override
    public <T extends Node> void traverse(TraverseType traverseType, T node) throws InvalidOperationException {
        BTreeNode bTreeNode = (BTreeNode) node;
        if (TraverseType.PRE_ORDER == traverseType) {
            preOrder(bTreeNode);
        } else if (TraverseType.IN_ORDER == traverseType) {
            inOrder(bTreeNode);
        } else if (TraverseType.POST_ORDER == traverseType) {
            postOrder(bTreeNode);
        } else if (TraverseType.LEVEL_ORDER == traverseType) {
            levelOrder(bTreeNode);
        } else {
            throw new InvalidOperationException(String.format("Given traversal type %s not supported", traverseType));
        }
    }

    public void traverse(BTreeNode node, TraverseType traverseType) {
        if (node == null) {
            return;
        }
        if (TraverseType.PRE_ORDER == traverseType) {
            System.out.println(node.toString());
        }
        traverse(node.getLeft(), traverseType);
        if (TraverseType.IN_ORDER == traverseType) {
            System.out.println(node.toString());
        }
        traverse(node.getRight(), traverseType);
        if (TraverseType.POST_ORDER == traverseType) {
            System.out.println(node.toString());
        }
    }

    public void preOrder(BTreeNode node) {
        Stack<BTreeNode> memory = new Stack<>();
        while (true) {
            while (node != null) {
                System.out.print(node.toString());
                memory.add(node);
                node = node.getLeft();
            }
            if (memory.isEmpty()) {
                break;
            }
            node = memory.pop().getRight();
        }
    }

    public void inOrder(BTreeNode node) {
        Stack<BTreeNode> memory = new Stack<>();
        while (true) {
            while (node != null) {
                memory.add(node);
                node = node.getLeft();
            }
            if (memory.isEmpty()) {
                break;
            }
            System.out.print(memory.peek().toString());
            node = memory.pop().getRight();
        }
    }

    public void postOrder(BTreeNode node) {
        Stack<BTreeNode> memory = new Stack<>();
        BTreeNode previous = null;
        while (true) {
            while (node != null) {
                memory.add(node);
                node = node.getLeft();
            }
            if (memory.isEmpty()) {
                break;
            }
            while (node == null && !memory.isEmpty()) {
                node = memory.peek();
                if (node.getRight() == null
                        || node.getRight().equals(previous)) {
                    System.out.print(node.toString());
                    memory.pop();
                    previous = node;
                    node = null;
                } else {
                    node = node.getRight();
                }
            }
        }
    }

    public void levelOrder(BTreeNode node) {
        Queue<BTreeNode> holder = new LinkedList<>();
        holder.offer(node);
        while (!holder.isEmpty()) {
            BTreeNode temp = holder.poll();
            System.out.print(temp);
            if (temp.getLeft() != null) {
                holder.offer(temp.getLeft());
            }
            if (temp.getRight() != null) {
                holder.offer(temp.getRight());
            }
        }
    }
}
