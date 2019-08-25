package com.datastructures.unionfind.unions;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final WeightedQuickUnionUF SITE_ROUTES;

    private boolean[][] status;
    private final int top;
    private final int bottom;
    private int openSites = 0;
    private final int n;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Cannot hold 0/-Ve array size");
        }

        SITE_ROUTES = new WeightedQuickUnionUF((n * n) + 2);
        status = new boolean[n][n];
        this.n = n;
        top = 0;
        bottom = (n * n) + 1;
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isNotBounded(row, col)) {
            throw new IllegalArgumentException("row /column should be in range of 0 to "
                    + (n - 1));
        }
        if (isOpen(row, col)) {
            return;
        }
        connect(row, col);
        status[row - 1][col - 1] = true;
        openSites += 1;
    }

    private void connect(int row, int col) {
        int mid = computeSiteIndex(row, col);
        if (row == 1) {
            SITE_ROUTES.union(mid, top);
        }
        if (row == n) {
            SITE_ROUTES.union(bottom, mid);
        }
        connectToDestination(mid, row, col - 1);
        connectToDestination(mid, row, col + 1);
        connectToDestination(mid, row - 1, col);
        connectToDestination(mid, row + 1, col);
    }

    private void connectToDestination(int source, int row, int col) {
        int unionIndex = computeSiteIndex(row, col);
        if (unionIndex >= 0 && isOpen(row, col)) {
            SITE_ROUTES.union(source, unionIndex);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (isNotBounded(row, col)) {
            throw new IllegalArgumentException("row /column should be in range of 0 to "
                    + (status.length - 1));
        }
        return status[row - 1][col - 1];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (isNotBounded(row, col)) {
            throw new IllegalArgumentException("row /column should be in range of 0 to "
                    + (status.length - 1));
        }
        if (isOpen(row, col)) {
            return SITE_ROUTES.connected(computeSiteIndex(row, col), top);
        }
        return false;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return SITE_ROUTES.connected(top, bottom);
    }

    private int computeSiteIndex(int row, int col) {
        if (isNotBounded(row, col)) {
            return -1;
        }
        return (n * (row - 1)) + col;
    }

    private boolean isNotBounded(int row, int col) {
        return row < 1 || row > n || col < 1 || col > n;
    }

    //Optional and no test
    public static void main(String[] args) {
        //NOOP
    }
}
