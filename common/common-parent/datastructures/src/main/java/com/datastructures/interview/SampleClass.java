package com.datastructures.interview;

import com.datastructures.unionfind.unions.WeightedQuickUnion;

import java.util.List;

public class SampleClass {
    public static int findCriticalRoads(List<List<Integer>> roads) {
        WeightedQuickUnion quickUnion = new WeightedQuickUnion(roads.size());
        for (int i = 0; i < roads.size(); i++) {
            for (int j = 0; i < roads.get(i).size(); i++) {
                quickUnion.union(i, roads.get(i).get(j));
            }
        }
        return 0;
    }
}
