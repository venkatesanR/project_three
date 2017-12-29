package com.datastructures.graphs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.datastructures.graphs.DATA.GEO;

public class DriverClass {
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unchecked")
		List<String> details = FileUtils.readLines(new File(
				"/home/YUME.COM/vrengasamy/intelijworkspace/project_three/common/common-parent/test_files/graph.txt"));
		int v = Integer.valueOf(details.get(0));
		int w = Integer.valueOf(details.get(1));
		Graph g = new Graph(v);
		for (int j = 0; j < v; j++) {
			g.addVertex(GEO.getNameById(j));
		}
		for (int i = 2; i < details.size(); i++) {
			String[] e = details.get(i).split(" ");
			g.addEdge(Integer.valueOf(e[0]), Integer.valueOf(e[1]),
					DATA.getDistance(Integer.valueOf(e[0]).intValue(), Integer.valueOf(e[1]).intValue()));
		}
		System.out.println("---------- DFS START----------------------");
		GraphSearchUtil.DFS(g, 2);
		System.out.println("---------- DFS END------------------------");

		System.out.println("---------- BFS START----------------------");
		GraphSearchUtil.BFS(g, 2);
		System.out.println("---------- BFS END------------------------");

	}
}
