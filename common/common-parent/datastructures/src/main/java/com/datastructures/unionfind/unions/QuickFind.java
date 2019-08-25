package com.datastructures.unionfind.unions;

public class QuickFind extends AbstractUnion {
    public QuickFind(Integer totalNodes) {
        super(totalNodes);
    }

    @Override
    public Boolean isConnected(int source, int destination) {
        return elements[source] == elements[destination];
    }

    @Override
    public void union(int source, int destination) {
        if (isConnected(source, destination)) {
            System.out.println(String.format("Node [%d -> %d] is already connected", source, destination));
            return;
        }
        int nxtSource = elements[source];
        for (int i = 0; i < elements.length; i++) {
            if (nxtSource == elements[i]) {
                elements[i] = elements[destination];
            }
        }
    }
}
