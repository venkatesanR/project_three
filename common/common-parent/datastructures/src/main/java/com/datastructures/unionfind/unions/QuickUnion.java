package com.datastructures.unionfind.unions;

public class QuickUnion extends AbstractUnion {
    private static final int DEFAULT_UNION_SIZE = 10;

    public QuickUnion() {
        this(DEFAULT_UNION_SIZE + 1);
    }

    public QuickUnion(Integer totalNodes) {
        super(totalNodes);
    }

    @Override
    public Boolean isConnected(int source, int destination) {
        return root(source) == root(destination);
    }

    @Override
    public void union(int source, int destination) {
        elements[root(source)] = root(destination);
    }
}
