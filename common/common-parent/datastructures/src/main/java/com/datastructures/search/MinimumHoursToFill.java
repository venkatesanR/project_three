package com.datastructures.search;

import java.util.List;

public class MinimumHoursToFill {
    int totalCount = 0;
    int rows = 0;
    int cols = 0;

    int minimumHours(int rows, int columns, List<List<Integer>> grid) {
        totalCount = rows * columns;
        this.rows = rows;
        this.cols = columns;
        int counter = 0;
        while (totalCount == 0) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (grid.get(i).get(j) == 1) {
                        distributeFiles(i, j, grid);
                    }
                }
            }
            counter += 1;
        }
        return 0;
    }

    private void distributeFiles(int row, int col, List<List<Integer>> grid) {
        int top = row - 1 > 0 ? row - 1 : -1;
        int bottom = row + 1 < rows ? row + 1 : -1;
        int left = col - 1 > 0 ? row - 1 : -1;
        int right = col + 1 < cols ? row + 1 : -1;

        if (left > 0 && grid.get(left).get(col) == 0) {
            grid.get(left).set(col, 1);
            totalCount -= 1;
        }
        if (right > 0 && grid.get(right).get(col) == 0) {
            grid.get(right).set(col, 1);
            totalCount -= 1;
        }
        if (top > 0 && grid.get(row).get(top) == 0) {
            grid.get(row).set(top, 1);
            totalCount -= 1;
        }
        if (bottom > 0 && grid.get(row).get(bottom) == 0) {
            grid.get(left).set(bottom, 1);
            totalCount -= 1;
        }
    }

    private void initialCount(List<List<Integer>> grid) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid.get(i).get(j) == 1)
                    totalCount += 1;
            }
        }
    }
}
