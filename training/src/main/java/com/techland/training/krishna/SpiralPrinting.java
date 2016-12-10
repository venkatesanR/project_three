package com.techland.training.krishna;

import java.util.ArrayList;

import javax.swing.text.Position;

public class SpiralPrinting {
	
	static SpiralPrinting position = new SpiralPrinting();
	private int rowStart;
	private int columnStart;
	
	public static SpiralPrinting increment(int rowStart, int columnStart){	
		while (columnStart != Matrix.c){
			System.out.println(Matrix.matrix[rowStart][columnStart]);
			columnStart = columnStart+1;
		}
		rowStart=rowStart+1;
		columnStart=columnStart-1;
		
		while (rowStart != Matrix.r){
			System.out.println(Matrix.matrix[rowStart][columnStart]);
			rowStart=rowStart+1;
		}
		rowStart=rowStart-1;
		
		position.rowStart=rowStart;
		position.columnStart=columnStart;
		return position;
	}
	
	public static void decrement(int rowStart,int columnStart){
		rowStart = position.rowStart;
		columnStart = position.columnStart;
		
		columnStart=columnStart-1;
		while (columnStart <= Matrix.c){
			if (columnStart < 0){
				break;
			}
			System.out.println(Matrix.matrix[rowStart][columnStart]);
			columnStart = columnStart-1;

		}
		rowStart=rowStart-1;
		columnStart=columnStart+1;
		while (rowStart != Matrix.r){
			if (rowStart < 0){
				break;
			}
			System.out.println(Matrix.matrix[rowStart][columnStart]);
			rowStart=rowStart-1;
		}
	}
	
	public static void main(String[] args) {
		int rowStart=0;
		int columnStart=0;
		Matrix.arrayDecleration();
		increment(rowStart,columnStart);
		decrement(rowStart,columnStart);

			

	}

}
