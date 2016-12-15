package com.techland.training.krishna;

public class SpiralPrintingNew {

	static int rowStart = 0;
	static int columnStart = 0;
	static int rowEnd = Matrix.r - 1;
	static int columnEnd = Matrix.c - 1;
	static String direction = "clockwise";

	public static void printing(int row, int column) {
		int count = 0;
		for (int i = 0; i < Matrix.r * Matrix.c; i++) {
			System.out.println("value of i is " + i);
			if (row < rowEnd) {
				if (column < columnEnd) {
					System.out.println(Matrix.matrix[row][column]);
					column = column + 1;
				} else if (row < rowEnd) {
					System.out.println(Matrix.matrix[row][column]);
					row = row + 1;
				}
			} else if (row > rowStart) {
				if (column > columnStart) {
					System.out.println(Matrix.matrix[row][column]);
					column = column - 1;
				} else {
//					System.out.println("value of i is " + i);
					while (row > rowStart) {
//						System.out.println("value of i is " + i);
						System.out.println(Matrix.matrix[row][column]);
						count = count + 1;
						row = row - 1;
					}
//					System.out.println(count);
//					System.out.println("value of i is " + i);
					i = i + count;
					row = row + 1;
					column = column + 1;
					columnEnd = columnEnd - 1;
					rowEnd = rowEnd - 1;
					rowStart = rowStart + 1;
					columnStart = columnStart + 1;
				}
			} else {
				System.out.println(Matrix.matrix[row][column]);
			}
		}
		// System.out.println(row);
		// System.out.println(column);
	}

	public static void main(String[] args) {
		Matrix.arrayDecleration();
		printing(rowStart, columnStart);
	}

}
