package com.datastructures.unionfind.unions;

import com.techmania.common.util.misc.ObjectUtil;

public class DriverModule {
    public static void main(String[] args) {
        DriverModule module = new DriverModule();
        AbstractUnion weightedUnion = ObjectUtil.newInstance(WeightedQuickUnion.class, 9);
        module.connect(weightedUnion);
        module.validate(weightedUnion);
    }

    private <T extends AbstractUnion> void validate(T unionFind) {
        System.out.println("Started validating Class: " + unionFind.getClass().getSimpleName());
        System.out.println(unionFind.toString());
        System.out.println(String.format("Check if [7 <-> 3 ? %s]", unionFind.isConnected(7, 3)));
        unionFind.union(7, 3);
        System.out.println("After connecting 7 and 3");
        System.out.println(String.format("Check if [7 <-> 3 ? %s]", unionFind.isConnected(7, 3)));
        System.out.println(unionFind.toString());
        System.out.println("Completed validating Class: " + unionFind.getClass().getSimpleName());
    }

    private <T extends AbstractUnion> void connect(T... unionFinds) {
        for (AbstractUnion unionFind : unionFinds) {
            unionFind.union(0, 5);
            unionFind.union(5, 6);
            unionFind.union(6, 1);
            unionFind.union(1, 2);
            unionFind.union(2, 7);
            unionFind.union(3, 4);
            unionFind.union(3, 8);
            unionFind.union(4, 9);
        }
    }
}
