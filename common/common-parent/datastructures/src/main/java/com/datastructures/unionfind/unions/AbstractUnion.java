package com.datastructures.unionfind.unions;


import java.util.Arrays;

public abstract class AbstractUnion {
    protected int[] elements;

    public AbstractUnion(Integer totalNodes) {
        elements = new int[totalNodes + 1];
        for (int i = 0; i <= totalNodes; i++) {
            elements[i] = i;
        }
    }

    public abstract void union(int source, int destination);

    public abstract Boolean isConnected(int source, int destination);

    public int count() {
        return elements.length;
    }

    /**
     * <p>
     * Check for the grant parent(element) that further don't have
     * any ancestor.(node index and values are same.)
     * </p>
     *
     * @param leaf or child node
     * @return parent into the depth
     */
    public int root(int child) {
        while (child != elements[child]) {
            //Assign child to find their corresponding ancestor
            child = elements[child];
        }
        return elements[child];
    }

    @Override
    public String toString() {
        return Arrays.toString(elements);
    }

}
