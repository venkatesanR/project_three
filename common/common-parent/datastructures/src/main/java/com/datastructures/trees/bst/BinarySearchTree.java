package com.datastructures.trees.bst;

import com.datastructures.models.BTreeNode;
import com.datastructures.trees.btree.ITraversalFactory;
import com.datastructures.trees.btree.TraversalFactory;
import com.datastructures.trees.btree.TreeTypeEnum;
import com.datastructures.trees.model.TraverseType;
import com.techmania.common.exceptions.InvalidOperationException;

public class BinarySearchTree<DataType extends Comparable> {
    private BTreeNode<DataType> root;
    private ITraversalFactory traversalFactory = new TraversalFactory()
            .getTraversalFactory(TreeTypeEnum.BINARY_TREE);

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

    public void print(boolean recursive) throws InvalidOperationException {
        if (recursive) {
            traversalFactory.traverse(TraverseType.PRE_ORDER, root);
            System.out.println("\n");
            traversalFactory.traverse(TraverseType.IN_ORDER, root);
            System.out.println("\n");
            traversalFactory.traverse(TraverseType.POST_ORDER, root);
        }
    }


    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = new BinarySearchTree<>();
        try {
            tree.add(50);
            tree.add(30);
            tree.add(20);
            tree.add(40);
            tree.add(70);
            tree.add(60);
            tree.add(80);
            tree.print(true);
        } catch (InvalidOperationException e) {
            e.printStackTrace();
        }
    }
}
