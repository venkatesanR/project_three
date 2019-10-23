package com.datastructures.queue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DriverClass {
    public static void main(String[] args) throws IOException {
        String q1 = "(3,2),(7,9),(19,0),(1,8)";
        String[] a = q1.split("\\),");
        System.out.println(getCoordinates(q1));

    }

    /**
     * Consider there are N steps up there if we need to reach top
     * We want to ensure we reached top is (N+1).
     * <p>
     * Since its dynamic ways of counting moves we need to calculate
     * step recursively with one and two steps.
     */
    private static int numberOfWaysToReachTop(int stepSize) {
        return steps(stepSize + 1);
    }

    private static int steps(int n) {
        //base case
        if (n <= 1) {
            return n;
        }
        //Divide problem by ste and collect it recursive
        return steps(n - 1) + steps(n - 2);
    }

    /**
     * According to pythogaran theorem if given four points is
     * square if its sahare the same diagonal.
     * <p>
     * Pythogaren distance = tanget2=sqrt(sideOne2+sideTwo2)
     */
    private static boolean isSquare(List<Coordinate> coordinates) {
        if (coordinates.isEmpty() || coordinates.size() != 4) {
            return false;
        }

        Coordinate c1 = coordinates.get(0);
        Coordinate c2 = coordinates.get(1);
        Coordinate c3 = coordinates.get(2);
        Coordinate c4 = coordinates.get(3);

        //Compute straight line distance and compare with tangent
        int d2 = distanceSquare(c1, c2);
        int d3 = distanceSquare(c1, c3);
        int d4 = distanceSquare(c1, c4);

        if (d2 == d3 && 2 * d2 == d4
                && 2 * distanceSquare(c2, c4) == distanceSquare(c2, c3)) {
            return true;
        }

        if (d3 == d4 && 2 * d3 == d2
                && 2 * distanceSquare(c3, c2) == distanceSquare(c3, c4)) {
            return true;
        }
        if (d2 == d4 && 2 * d2 == d3
                && 2 * distanceSquare(c2, c3) == distanceSquare(c2, c4)) {
            return true;
        }

        return false;
    }

    private static List<Coordinate> getCoordinates(String points) {
        List<Coordinate> coordinates = new ArrayList<>();
        String[] locs = points.split("\\),");
        for (String point : locs) {
            point = point.replace("(", "");
            point = point.replace(")", "");
            String[] xAndY = point.split(",");
            coordinates.add(new Coordinate(Integer.valueOf(xAndY[0]), Integer.valueOf(xAndY[1])));
        }
        return coordinates;
    }

    /**
     * Euler distance
     */
    private static int distanceSquare(Coordinate coordinate1, Coordinate coordinate2) {
        return (coordinate2.x - coordinate1.x) * (coordinate2.x - coordinate1.x)
                + (coordinate2.y - coordinate1.y) * (coordinate2.y - coordinate1.y);
    }

    static class Coordinate {
        int x;
        int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
