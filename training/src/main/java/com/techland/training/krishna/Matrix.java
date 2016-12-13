package com.techland.training.krishna;

public class Matrix {
		
	static int r=5;
	static int c=5;
	static int[][] matrix = null;
	
	public static int[][] arrayDecleration() {
		if(matrix==null) {
			matrix=new int[r][c];
			matrix[0][0]=1;
			matrix[0][1]=2;
			matrix[0][2]=3;
			matrix[0][3]=4;
			matrix[0][4]=5;
			matrix[1][0]=6;
			matrix[1][1]=7;
			matrix[1][2]=8;
			matrix[1][3]=9;
			matrix[1][4]=10;
			matrix[2][0]=11;
			matrix[2][1]=12;
			matrix[2][2]=13;
			matrix[2][3]=14;
			matrix[2][4]=15;
			matrix[3][0]=16;
			matrix[3][1]=17;
			matrix[3][2]=18;
			matrix[3][3]=19;
			matrix[3][4]=20;
			matrix[4][0]=21;
			matrix[4][1]=22;
			matrix[4][2]=23;
			matrix[4][3]=24;
			matrix[4][4]=25;
		}
		return matrix;

	}

}
