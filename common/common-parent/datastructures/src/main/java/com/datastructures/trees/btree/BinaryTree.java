package com.datastructures.trees.btree;

import com.datastructures.models.BTreeNode;
import com.datastructures.trees.model.ITree;
import com.datastructures.trees.model.TraverseType;
import com.techmania.common.exceptions.InvalidOperationException;

public class BinaryTree<DataType> implements ITree<DataType> {
    private BTreeNode<DataType> root;
    private ITraversalFactory traversalFactory;

    public BinaryTree() {
        root = new BTreeNode<>(null);
        traversalFactory = new TraversalFactory()
                .getTraversalFactory(TreeTypeEnum.BINARY_TREE);
    }

    @Override
    public void add(DataType data) throws InvalidOperationException {

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
}
