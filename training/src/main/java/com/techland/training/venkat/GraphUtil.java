package com.techland.training.venkat;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class GraphUtil {
	private static final int FRAME_WIDTH = 300;
	private static final int FRAME_HEIGHT = 400;

	//Applied only for Int Testing purpose
	public static void showGraph(Comparable[] a) {
		JFrame frame = new JFrame();
		frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		frame.setTitle("Data");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BarGraph component = new BarGraph(FRAME_WIDTH, FRAME_HEIGHT);
		List<Integer> data = new ArrayList<Integer>();
		for (int i = 0; i < a.length; i++) {
			data.add((Integer) a[i]);
		}
		component.setData(data);
		frame.add(component);
		frame.setVisible(true);
	}
}
