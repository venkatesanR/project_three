package com.techland.training.krishna;

import java.util.Scanner;

public class OneDimensionalArray {
	public static void main(String[] args) {
		int[] a;   
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        a = new int[n];
        
        for(int i = 0 ; i < n; i++){
            int val = scan.nextInt();
            a[i] = val;
        }
        
        scan.close();

        for (int i = 0; i < a.length; i++) {
            System.out.println(a[i]);
        }
    }

}
