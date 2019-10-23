package com.datastructures.arrays.problems;

import java.util.Iterator;
import java.util.PriorityQueue;

public class PrintCombinationInOrder {
    private static String FORMAT = "%s^3 + %s^3 = %s";
    static long max = -1;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        print(10000);
        System.out.println((System.currentTimeMillis() - startTime) / 1000);
    }


    private static void print(int extreme) {
        PriorityQueue<CubeSum> queue = new PriorityQueue<CubeSum>();
        for (int left = 0; left <= extreme; left++) {
            for (int mover = 0; mover <= left; mover++) {
                queue.offer(new CubeSum(mover, left));
            }
        }
        //queue.stream().forEach(sum -> System.out.println(sum));
    }


    private static void printPQ(int n) {
        PriorityQueue<CubeSum> queue = new PriorityQueue<>();
        for (int i = 0; i <= n; i++) {
            queue.add(new CubeSum(i, i));
        }

        Iterator<CubeSum> itr = queue.iterator();
        while (itr.hasNext()) {
            CubeSum cubeSum = itr.next();
            System.out.println(itr.next());
            itr.remove();
            if (cubeSum.j < n) {
                queue.add(new CubeSum(cubeSum.i, cubeSum.j + 1));
            }
        }
    }

    static class CubeSum implements Comparable<CubeSum> {
        private final int sum;
        private final int i;
        private final int j;

        public CubeSum(int i, int j) {
            this.sum = i * i * i + j * j * j;
            this.i = i;
            this.j = j;
        }

        public int compareTo(CubeSum that) {
            if (this.sum < that.sum) {
                return -1;
            }
            if (this.sum > that.sum) {
                return +1;
            }
            return 0;
        }

        public String toString() {
            return sum + " = " + i + "^3" + " + " + j + "^3";
        }
    }
}
