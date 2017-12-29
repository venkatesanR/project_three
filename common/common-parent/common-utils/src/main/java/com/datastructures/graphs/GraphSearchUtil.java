package com.datastructures.graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.PriorityBlockingQueue;

import com.datastructures.utils.ArrayUtils;

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

	public static long[] dijkstras(Graph g, int source) {
		Queue<Vertex> queue = new PriorityQueue<Vertex>();
		queue.add(g.getVertex(source));
		long[] distance=new long[g.getV()];
		for (int i = 0; i < g.getV(); i++) {
			g.getVertex(i).setDistance(-1);
			distance[i]=-1;
		}
		g.getVertex(source).setDistance(0);
		while (!queue.isEmpty()) {
			Vertex u = queue.remove();
			List<Edge> adj = u.getAdjacent();
			if (adj == null || adj.isEmpty()) {
				continue;
			}
			for (Edge v : adj) {
				long distancex = (long) (u.getDistance() + v.getWeight().longValue());
				if (v.getIncident().getDistance() == -1 || v.getIncident().getDistance() > distancex) {
					g.getVertex(v.getIncident().getIndex()).setDistance(distancex);
					queue.add(g.getVertex(v.getIncident().getIndex()));
					distance[v.getIncident().getIndex()]=distancex;
				}
			}
		}
		return distance;
	}

}
