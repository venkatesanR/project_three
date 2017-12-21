package com.datastructures.graphs;

import java.io.IOException;

public class Solution {

	public static void main(String[] args) throws IOException {
		Graph g = new Graph(4);
		g.addEdge(0, 1);
		g.addEdge(0, 2);
		g.addEdge(1, 2);
		g.addEdge(2, 0);
		g.addEdge(2, 3);
		g.addEdge(3, 3);
		System.out.println("Following is Depth First Traversal " + "(starting from vertex 2)");
		g.traverse(IGraph.TRAVERSE.DFS, 2);
		g.traverse(IGraph.TRAVERSE.BFS, 2);
	}

}
