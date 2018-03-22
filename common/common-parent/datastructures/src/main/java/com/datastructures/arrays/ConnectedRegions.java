package com.datastructures.arrays;

import java.util.Scanner;

public class ConnectedRegions {
	public static boolean[][] visited = null;
	static int[][] location = new int[][] { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 },
			{ 1, 1 } };
	static int[][] arr = null;
	public static int n = 0;
	public static int m = 0;
	public static int max = 0;
	public static int count = 0;

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		n = in.nextInt();
		m = in.nextInt();
		int[][] matrix = new int[n][m];
		for (int matrix_i = 0; matrix_i < n; matrix_i++) {
			for (int matrix_j = 0; matrix_j < m; matrix_j++) {
				matrix[matrix_i][matrix_j] = in.nextInt();
			}
		}
		int result = connectedCell(matrix);
		System.out.println(result);
		add(new Number() {

			@Override
			public long longValue() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int intValue() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public float floatValue() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double doubleValue() {
				// TODO Auto-generated method stub
				return 0.3;
			}
		});
		in.close();
	}

	private static <T extends Number> void add(T d) {
		System.out.println(d.doubleValue());
	}

	private static int connectedCell(int[][] matrix) {
		visited = new boolean[n][m];
		arr = matrix;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (arr[i][j] == 1) {
					matchRegion(i, j);
				}
			}
		}
		return max;
	}

	// Place to connect All 1s in Array
	public static void matchRegion(int i, int j) {
		if (visited[i][j]) {
			return;
		}
		visited[i][j] = true;
		int i1 = 0;
		int j1 = 0;
		count += 1;
		if (count > max) {
			max = count;
		}
		for (int x = 0; x < location.length; x++) {
			i1 = i + location[x][0];
			j1 = j + location[x][1];
			int val = getValue(i1, j1);
			if (val > 0 && !visited[i1][j1]) {
				matchRegion(i1, j1);
			}
		}
		count = 0;
		visited[i][j] = false;
	}

	private static int getValue(int i, int j) {
		if (i < 0 || i >= n || j < 0 || j >= m) {
			return 0;
		}
		return arr[i][j];
	}
}
