package com.datastructures.graphs;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class GraphSearchUtil {
	static boolean[] visited = null;
	static double distance = 0;

	public static void DFS(Graph g, int start) {
		if (visited == null) {
			visited = new boolean[g.getV()];
		}
		visited[start] = true;
		Collection<Edge> adj = g.neigbours(start);
		System.out.println(g.getVertex(start).getData());
		if (adj != null && !adj.isEmpty()) {

			for (Edge sub : adj) {
				if (!visited[sub.getIncident().getIndex()]) {
					DFS(g, sub.getIncident().getIndex());
				}
			}
		}
	}

	public static void BFS(Graph g, int start) {
		if (visited == null) {
			visited = new boolean[g.getV()];
		}
		Arrays.fill(visited, false);
		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(g.getVertex(start));
		visited[g.getVertex(start).getIndex()] = true;
		while (!queue.isEmpty()) {
			int topIndex = queue.remove().getIndex();
			System.out.println(g.getVertex(topIndex).getData());
			Collection<Edge> adj = g.getVertex(topIndex).getAdjacent();
			if (adj == null || adj.isEmpty()) {
				continue;
			}
			for (Edge e : adj) {
				if (!visited[e.getIncident().getIndex()]) {
					queue.add(e.getIncident());
				}
				visited[e.getIncident().getIndex()] = true;
			}
		}
	}

}
