package com.datastructures.trees.btree;

public class TraversalFactory {
    private ITraversalFactory traversalFactory;

    public ITraversalFactory getTraversalFactory(TreeTypeEnum treeTypeEnum) {
        if (TreeTypeEnum.BINARY_TREE == treeTypeEnum) {
            return getBinaryTraversal();
        }
        return null;
    }

    private ITraversalFactory getBinaryTraversal() {
        return new BinaryTraversal();
    }
}
