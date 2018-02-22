package com.datastructures.codility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * City connection Solution Codility
 * 
 * @author vrengasamy
 *
 */
public class Graph {
	private List<Integer>[] adj;
	private int V;
	private int E;

	public Graph(int v) {
		adj = new List[v];
		this.V = v;
	}

	public void addEdge(int a, int b) {
		if (adj[a] == null) {
			adj[a] = new ArrayList<Integer>();
		}
		if (adj[b] == null) {
			adj[b] = new ArrayList<Integer>();
		}
		adj[a].add(b);
		adj[b].add(a);
	}

	public long[] dijikstra(int source) {
		Queue<Integer> queue = new PriorityQueue<>();
		queue.add(source);
		long[] distance = new long[V];
		Arrays.fill(distance, -1);
		distance[source] = 0;
		while (!queue.isEmpty()) {
			Integer itNode = queue.remove();
			if (adj[itNode] == null || adj[itNode].isEmpty())
				continue;
			for (Integer adjNode : adj[itNode]) {
				long distanceX = distance[itNode] + 1;
				if (distance[adjNode] == -1 || distance[adjNode] > distanceX) {
					queue.add(adjNode);
					distance[adjNode] = distanceX;
				}
			}
		}
		return distance;
	}

	public static void main(String[] args) {
		Graph g = new Graph(10);
		long[] distanceSummary = new long[9];
		int[] edge = { 9, 1, 4, 9, 0, 4, 8, 9, 0, 1 };
		int source = -1;

		for (int i = 0; i < edge.length; i++) {
			if (edge[i] != i) {
				g.addEdge(i, edge[i]);
			} else {
				source = i;
			}
		}
		long[] distance = g.dijikstra(source);
		for (int i = 0; i < distance.length; i++) {
			if (distance[i] > 0) {
				distanceSummary[(int) (distance[i] - 1)]++;
			}
		}
		for (long dis : distanceSummary) {
			System.out.println(dis);
		}
	}
}
