package com.datastructures.graphs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class DriverClass {
	public static void main(String[] args) throws IOException {
		List<String> details = FileUtils.readLines(
				new File("/home/YUME.COM/vrengasamy/intelijworkspace/project_three/common/documents/graph.txt"));
		int v = Integer.valueOf(details.get(0));
		int w = Integer.valueOf(details.get(1));
		Graph g = new Graph(v);
		for (int j = 0; j < v; j++) {
			g.addVertex(j);
		}
		for (int i = 2; i < details.size(); i++) {
			String[] e = details.get(i).split(" ");
			g.addEdge(Integer.valueOf(e[0]), Integer.valueOf(e[1]));
		}
		System.out.println(g.toString());
		//System.out.println(g.maxDegree());

	}
}
