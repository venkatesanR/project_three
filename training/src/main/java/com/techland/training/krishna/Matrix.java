package com.techland.training.krishna;

public class Matrix {
		
	static int r=3;
	static int c=3;
	static int[][] matrix = null;
	
	public static int[][] arrayDecleration() {
		if(matrix==null) {
			matrix=new int[r][c];
			matrix[0][0]=1;
			matrix[0][1]=2;
			matrix[0][2]=3;
			matrix[1][0]=4;
			matrix[1][1]=5;
			matrix[1][2]=6;
			matrix[2][0]=7;
			matrix[2][1]=8;
			matrix[2][2]=9;	
		}
		return matrix;

	}

}
