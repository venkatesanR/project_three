package com.datastructures.trees.btree;

import com.datastructures.models.BTreeNode;
import com.datastructures.trees.model.ITree;
import com.datastructures.trees.model.TraverseType;
import com.techmania.common.exceptions.InvalidOperationException;

import java.util.LinkedList;
import java.util.Queue;

public class BinaryTree<DataType> implements ITree<DataType> {
    private BTreeNode<DataType> root;
    private ITraversalFactory traversalFactory;

    public BinaryTree() {
        traversalFactory = new TraversalFactory()
                .getTraversalFactory(TreeTypeEnum.BINARY_TREE);
    }

    @Override
    public void add(DataType data) throws InvalidOperationException {
        Queue<BTreeNode> holder = new LinkedList<>();
        if (root == null) {
            root = new BTreeNode<>(data);
            return;
        }
        holder.add(root);
        while (!holder.isEmpty()) {
            BTreeNode peek = holder.poll();
            if (peek.getLeft() != null) {
                holder.offer(peek.getLeft());
            } else {
                peek.setLeft(new BTreeNode<>(data));
                break;
            }
            if (peek.getRight() != null) {
                holder.offer(peek.getRight());
            } else {
                peek.setRight(new BTreeNode<>(data));
                break;
            }
        }
    }

    @Override
    public void delete(DataType data) throws InvalidOperationException {

    }

    @Override
    public void search(DataType data) throws InvalidOperationException {

    }

    @Override
    public void traverse(TraverseType traverseType, BTreeNode node) throws InvalidOperationException {
        traversalFactory.traverse(traverseType, node);
    }

    public static void main(String[] args) throws InvalidOperationException {
        BinaryTreeProblems binaryTreeProblems = new BinaryTreeProblems();
        BinaryTree<Integer> tree = new BinaryTree<>();
        tree.add(1);

        tree.add(2);
        tree.add(3);

        tree.add(4);
        tree.add(5);
        tree.add(6);
        tree.add(7);
        tree.add(8);
        System.out.println(tree);
        System.out.println(binaryTreeProblems.size(tree.root));
    }
}
