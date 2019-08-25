package com.datastructures.unionfind.unions;

public class WeightedQuickUnion extends AbstractUnion {
    private long[] weight = null;

    public WeightedQuickUnion(Integer totalNodes) {
        super(totalNodes);
        weight = new long[totalNodes + 1];
    }

    @Override
    public Boolean isConnected(int source, int destination) {
        return root(source) == root(destination);
    }

    /**
     * Implementation done by accounting path compression
     *
     * @param source
     * @param destination
     */
    @Override
    public void union(int source, int destination) {
        int sourcert = root(source);
        int destrt = root(destination);
        if (sourcert == destrt) {
            return;
        }
        if (weight[sourcert] > weight[destrt]) {
            elements[destrt] = sourcert;
            elements[sourcert] += elements[destrt];

        } else {
            elements[sourcert] = destrt;
            elements[destrt] += elements[sourcert];
        }
    }

    @Override
    public int root(int child) {
        while (child != elements[child]) {
            elements[child] = elements[elements[child]];
            child = elements[child];
        }
        return elements[child];
    }
}
