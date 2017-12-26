package com.datastructures.graphs;

import java.util.Collection;

public class GraphSearchUtil {
	static boolean[] visted = null;
	static double distance = 0;

	public static void  search(Graph g, int start) {
		visted = new boolean[g.getV()];
		 DFS(g, start);
	}

	public static double search(Graph g, int start, int end) {
		visted = new boolean[g.getV()];
		return DFS(g, start, end);
	}
	public static void  DFS(Graph g, int start) {
		visted[start] = true;
		Collection<Edge> adj = g.neigbours(start);
		System.out.println(g.getVertex(start).getData());
		for (Edge sub : adj) {
			if (!visted[sub.getIncident().getIndex()]) {
				 DFS(g, sub.getIncident().getIndex());
			}
		}
	}
	
	public static double DFS(Graph g, int start, int end) {
		visted[start] = true;
		Collection<Edge> adj = g.neigbours(start);
		System.out.println(g.getVertex(start).getData());
		for (Edge sub : adj) {
			if (!visted[sub.getIncident().getIndex()]) {
				distance = distance + Double.valueOf(sub.getWeight().toString());
				if (sub.getIncident().getIndex() == end) {
					return distance;
				}
				return DFS(g, sub.getIncident().getIndex(), end);
			}
		}
		return distance;
	}
}
