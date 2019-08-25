package com.datastructures.trees.btree;

import com.datastructures.models.Node;
import com.datastructures.trees.model.TraverseType;
import com.techmania.common.exceptions.InvalidOperationException;

public interface ITraversalFactory {
    public <T extends Node> void traverse(TraverseType traverseType, T node) throws InvalidOperationException;
}