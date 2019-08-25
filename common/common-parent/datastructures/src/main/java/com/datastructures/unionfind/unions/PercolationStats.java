package com.datastructures.unionfind.unions;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final double mean;
    private final double stddev;
    private final double confidenceHi;
    private final double confidenceLo;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("Atleast one time event trail should defined");
        }
        double[] percolationThresholds = new double[T];
        for (int i = 0; i < T; i++) {
            Percolation percolation = new Percolation(N);
            int runs = 0;
            while (!percolation.percolates()) {
                int column;
                int row;
                do {
                    column = 1 + StdRandom.uniform(N);
                    row = 1 + StdRandom.uniform(N);
                } while (percolation.isOpen(row, column));
                percolation.open(row, column);
                runs++;
            }
            percolationThresholds[i] = runs / (double) (N * N);
        }

        mean = StdStats.mean(percolationThresholds);
        stddev = StdStats.stddev(percolationThresholds);
        double confidenceFraction = (1.96 * stddev()) / Math.sqrt(T);
        confidenceLo = mean - confidenceFraction;
        confidenceHi = mean + confidenceFraction;
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLo;

    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHi;

    }

    // test client (optional)
    public static void main(String[] args) {
        int gridLength = 200;
        int numberOfOperations = 100;
        PercolationStats stats = new PercolationStats(gridLength, numberOfOperations);
        System.out.println(String.format("mean = %s", stats.mean()));
        System.out.println(String.format("stddev = %s", stats.stddev()));
        System.out.println("95% ".concat(String.format("confidence interval = [%s, %s]", stats.confidenceLo(),
                stats.confidenceHi())));
    }
}