package com.techmania.java.bestpractice;

import java.util.HashSet;
import java.util.Set;

public class SampleClass {

	public static void main(String[] args) {
		Set<Man> manS = new HashSet<>();
		Man oneM = new Man();
		oneM.setId(1);
		Set<String> datas = new HashSet<>();
		datas.add("A1");
		datas.add("A2");
		datas.add("A3");
		oneM.setDatas(datas);
		
		
		Man two = new Man();
		two.setId(1);
		Set<String> twoD = new HashSet<>();
		twoD.add("A1");
		twoD.add("A2");
		twoD.add("A5");
		two.setDatas(twoD);
		
		
		manS.add(oneM);
		manS.add(two);
		for (Man m : manS) {
			for (String s1 : m.getDatas()) {
				System.out.println(s1);
			}
		}
	}
}
