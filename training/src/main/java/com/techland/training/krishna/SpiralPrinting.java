package com.techland.training.krishna;

import java.util.ArrayList;

public class SpiralPrinting {
	static ArrayList<Integer> rowList = new ArrayList<Integer>();
	static ArrayList<Integer> columnList = new ArrayList<Integer>();
	static int rowStart = 0;
	static int columnStart = 0;
	static int rowEnd = Matrix.r - 1;
	static int columnEnd = Matrix.c - 1;

	public static void increment(int rowStart, int columnStart) {
		while (!columnList.contains(columnStart)) {
			System.out.println(Matrix.matrix[rowStart][columnStart]);
			columnStart = columnStart + 1;
			if (columnStart == Matrix.c) {
				break;
			}
		}rowList.add(rowStart);
		columnStart = columnStart - 1;
		rowStart = rowStart + 1;
		
		while (!rowList.contains(rowStart)) {
			System.out.println(Matrix.matrix[rowStart][columnStart]);
			rowStart = rowStart + 1;
			if (rowStart == Matrix.r) {
				break;
			}
		}columnList.add(columnStart);
	}

	public static void decrement(int rowend, int columnend) {
		columnend = columnend - 1;
		while (!columnList.contains(columnend)) {
			System.out.println(Matrix.matrix[rowend][columnend]);
			columnend = columnend - 1;
			if (columnend == columnStart - 1) {
				break;
			}
		}rowList.add(rowend);
		columnend = columnend + 1;
		rowend = rowend - 1;
		
		while (!rowList.contains(rowend)) {
			System.out.println(Matrix.matrix[rowend][columnend]);
			rowend = rowend - 1;
			if (rowend == rowStart - 1) {
				break;
			}
		}columnList.add(columnStart);
	}

	public static void main(String[] args) {
		Matrix.arrayDecleration();
		if (Matrix.r == 1) {
			while (columnStart != Matrix.c) {
				System.out.println(Matrix.matrix[rowStart][columnStart]);
				columnStart = columnStart + 1;
			}
		} else if (Matrix.c == 1) {
			while (rowStart != Matrix.r) {
				System.out.println(Matrix.matrix[rowStart][columnStart]);
				rowStart = rowStart + 1;
			}
		} else {
			while (rowList.size() < Matrix.r && columnList.size() < Matrix.c) {
				increment(rowStart, columnStart);
				decrement(rowEnd, columnEnd);
				rowStart = rowStart + 1;
				rowEnd = rowEnd - 1;
				columnStart = columnStart + 1;
				columnEnd = columnEnd - 1;
			}
		}
	}
}
