package com.datastructures.trees.model;

import com.datastructures.models.BTreeNode;
import com.techmania.common.exceptions.InvalidOperationException;

public interface ITree<DataType> {
    public void add(DataType data) throws InvalidOperationException;

    public void delete(DataType data) throws InvalidOperationException;

    public void search(DataType data) throws InvalidOperationException;

    public <T extends BTreeNode> void traverse(TraverseType traverseType, T node)
            throws InvalidOperationException;
}
