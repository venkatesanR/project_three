package com.datastructures.trees.btree;

import com.datastructures.models.BTreeNode;
import com.datastructures.models.Node;
import com.datastructures.trees.model.TraverseType;
import com.techmania.common.exceptions.InvalidOperationException;

public class BinaryTraversal implements ITraversalFactory {
    @Override
    public <T extends Node> void traverse(TraverseType traverseType, T node) throws InvalidOperationException {
        BTreeNode bTreeNode = (BTreeNode) node;
        if (TraverseType.IN_ORDER == traverseType) {
            inOrderTraversal(bTreeNode);
        } else if (TraverseType.PRE_ORDER == traverseType) {
            preOrderTraversal(bTreeNode);
        } else if (TraverseType.POST_ORDER == traverseType) {
            postOrderTraversal(bTreeNode);
        } else {
            throw new InvalidOperationException(String.format("Given traversal type %s not supported", traverseType));
        }
    }

    public void inOrderTraversal(BTreeNode node) {

    }

    public void preOrderTraversal(BTreeNode node) {

    }

    public void postOrderTraversal(BTreeNode node) {

    }
}
