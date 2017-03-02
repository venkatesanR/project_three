package com.techland.training.venkat;

public class EquationSolver {
	public static void solveLinearEquation(int[] a, int[] b) {
		int a1 = a[0];
		int b1 = a[1];
		int a2 = b[0];
		int b2 = b[1];
		int c1 = a[2];
		int c2 = b[2];
		int delta = (a1 * b2 - b1 * a2);
		int delx = (c1 * b2 - c2 * b1);
		int dely = (a1 * c2 - a2 * c1);
		System.out.println("x :" + (delx / delta));
		System.out.println("y :" + (dely / delta));
	}
}
