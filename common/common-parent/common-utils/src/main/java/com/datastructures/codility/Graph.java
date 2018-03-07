package com.datastructures.codility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

/**
 * City connection Solution Codility
 * 
 * @author vrengasamy
 *
 */
public class Graph {
	private static List<Integer>[] adj;
	private static int V;
	private static int E;

	public static void addEdge(int a, int b) {
		if (adj[a] == null) {
			adj[a] = new ArrayList<Integer>();
		}
		if (adj[b] == null) {
			adj[b] = new ArrayList<Integer>();
		}
		adj[a].add(b);
		adj[b].add(a);
	}

	public static long[] dijikstra(int source) {
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
		Scanner scan = new java.util.Scanner(System.in);
		int n = scan.nextInt();
		adj = new List[n];
		V = n;
		
		long[] distanceSummary = new long[9];
		int source = -1;
		for (int i = 0; i < n; i++) {
			int edgeValue = scan.nextInt();
			if (edgeValue != i) {
				addEdge(i, edgeValue);
			} else {
				source = i;
			}
		}
		long[] distance = dijikstra(source);
		for (int i = 0; i < distance.length; i++) {
			if (distance[i] > 0) {
				distanceSummary[(int) (distance[i] - 1)]++;
			}
		}
		for (long dis : distanceSummary) {
			System.out.println(dis);
		}
		scan.close();
	}
}
