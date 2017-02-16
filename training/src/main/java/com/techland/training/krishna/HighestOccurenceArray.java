package com.techland.training.krishna;

import java.util.ArrayList;
import java.util.Collections;

// To find the highest value with high occurence of 2 arays.
public class HighestOccurenceArray {

	static int[] list1 = new int[5];
	static int[] list2 = new int[5];
	static ArrayList<Integer> list = new ArrayList<Integer>();
	
	public static void arrayDecleration(){
		list1[0]=1;
		list1[1]=5;
		list1[2]=4;
		list1[3]=5;
		list1[4]=3;
		
		list2[0]=2;
		list2[1]=1;
		list2[2]=0;
		list2[3]=5;
		list2[4]=4;
	}
	
	public static void main(String[] args) {
		arrayDecleration();
		for (int i=0; i<list1.length; i++){
			for (int j=0; j<list2.length; j++){
				if (list1[i] == list2[j]){
					list.add(list1[i]);
//					list.add(list2[j]);
				}
			}
		}
		Collections.sort(list);
		System.out.println(list.get(list.size()-1));
	}
}
