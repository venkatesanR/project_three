package com.datastructures.graphs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class DriverClass {
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unchecked")
		List<String> details = FileUtils.readLines(new File(
				"/home/YUME.COM/vrengasamy/intelijworkspace/project_three/common/common-parent/test_files/graph.txt"));
		String[] ve = details.get(0).split(" ");
		int v = Integer.valueOf(ve[0]);
		int e1=Integer.valueOf(ve[1]);;
		Graph g = new Graph(v);
		long t1=System.currentTimeMillis();
		for (int j = 0; j < v; j++) {
			g.addVertex(j);
		}
		for (int j = 1; j < details.size() - 1; j++) {
			String[] e = details.get(j).split(" ");
			int a = Integer.valueOf(e[0]);
			int b = Integer.valueOf(e[1]);
			g.addEdge(a - 1, b - 1, 1, true);
		}
		int s = Integer.valueOf(details.get(details.size() - 1));
		long[] data=GraphUtil.shortestPathUnweighted(g, s-1);
		data=GraphUtil.singleSourceOccurances(data);
		for(int i=0;i<data.length;i++) {
			if(i!=s-1) {
				System.out.println(data[i]);
			}
		}
		//double exeTime=(System.currentTimeMillis()-t1)/1000.0;
		//System.out.println(exeTime);
	}
}
