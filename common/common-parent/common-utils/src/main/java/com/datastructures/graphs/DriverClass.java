package com.datastructures.graphs;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.datastructures.graphs.DATA.GEO;

public class DriverClass {
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unchecked")
		List<String> details = FileUtils.readLines(new File(
				"/home/YUME.COM/vrengasamy/intelijworkspace/project_three/common/common-parent/test_files/graph.txt"));
		String[] ve = details.get(0).split(" ");
		int v = Integer.valueOf(ve[0]);
		Graph g = new Graph(v);
		for (int j = 0; j < v; j++) {
			g.addVertex(j);
		}
		for (int j = 1; j < details.size() - 1; j++) {
			String[] e = details.get(j).split(" ");
			int a = Integer.valueOf(e[0]);
			int b = Integer.valueOf(e[1]);
			double w = Double.valueOf(e[2]);
			g.addEdge(a - 1, b - 1, w);
		}
		int s = Integer.valueOf(details.get(details.size() - 1));
		long[] data=GraphSearchUtil.dijkstras(g, s-1);
		for(int i=0;i<data.length;i++) {
			if(i!=s-1) {
				System.out.println(data[i]);
			}
		}
	}
}
