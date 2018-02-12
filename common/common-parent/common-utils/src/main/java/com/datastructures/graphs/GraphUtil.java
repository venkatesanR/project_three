package com.datastructures.graphs;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class GraphUtil {
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

	/**
	 * Single Source Shortest PATH
	 * 
	 * @param g
	 * @param source
	 * @return
	 */
	public static long[] dijkstras(Graph g, int source) {
		Queue<Vertex> queue = new PriorityQueue<Vertex>();
		queue.add(g.getVertex(source));
		long[] distance = new long[g.getV()];
		for (int i = 0; i < g.getV(); i++) {
			g.getVertex(i).setDistance(-1);
			distance[i] = -1;
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
					distance[v.getIncident().getIndex()] = distancex;
				}
			}
		}
		return distance;
	}

	/**
	 * Johnny, like every mathematician, has his favorite sequence of distinct
	 * natural numbers. Let’s call this sequence . Johnny was very bored, so he
	 * wrote down copies of the sequence in his big notebook. One day, when
	 * Johnny was out, his little sister Mary erased some numbers(possibly zero)
	 * from every copy of and then threw the notebook out onto the street. You
	 * just found it. Can you reconstruct the sequence?
	 * 
	 * In the input there are sequences of natural numbers representing the
	 * copies of the sequence after Mary’s prank. In each of them all numbers
	 * are distinct. Your task is to construct the shortest sequence that might
	 * have been the original . If there are many such sequences, return the
	 * lexicographically smallest one. It is guaranteed that such a sequence
	 * exists.
	 * 
	 * Note Sequence is lexicographically less than sequence if and only if
	 * there exists such that for all .
	 * 
	 * Input Format
	 * 
	 * In the first line, there is one number denoting the number of copies of .
	 * This is followed by and in next line a sequence of length representing
	 * one of sequences after Mary's prank. All numbers are separated by a
	 * single space.
	 * 
	 * Constraints
	 * 
	 * 
	 * All values in one sequence are distinct numbers in range .
	 * 
	 * Output Format
	 * 
	 * In one line, write the space-separated sequence - the shortest sequence
	 * that might have been the original . If there are many such sequences,
	 * return the lexicographically smallest one.
	 * 
	 * Sample Input
	 * 
	 * 2 2 1 3 3 2 3 4 Sample Output
	 * 
	 * 1 2 3 4
	 */
	public static void topologicalSort() {

	}

	public int selfLoops(Graph g) {
		int count = 0;
		for (int i = 0; i < g.getV(); i++) {
			List<Edge> adj = g.getVertex(i).getAdjacent();
			if (adj == null || adj.isEmpty()) {
				continue;
			}
			for (int j = 0; j < adj.size(); j++) {
				if (adj.get(j).getIncident().equals(g.getVertex(i))) {
					count += 1;
				}
			}
		}
		return count / 2;
	}

	public int degree(Graph g, int i) {
		return g.getVertex(i).getAdjacent() != null ? g.getVertex(i).getAdjacent().size() : 0;
	}

	public int maxDegree(Graph g) {
		int max = 0;
		for (int i = 0; i < g.getV(); i++) {
			int newDeg = degree(g, i);
			if (newDeg > max)
				max = newDeg;
		}
		return max;
	}

	public int avgDegree(Graph g) {
		return 2 * g.getE() / g.getV();
	}

	/**
	 * Concept : Shortest Path in Unweighted Graph Let s be the input vertex
	 * from which we want to find the shortest path to all other vertices.
	 * Unweighted graph is a special case of the weighted shortest-path problem,
	 * with all edges a weight of 1. The algorithm is similar to BFS and we need
	 * to use the following data structures: • • A distance table with three
	 * columns (each row corresponds to a vertex): ○ Distance from source
	 * vertex. ○ Path – contains the name of the vertex through which we get the
	 * shortest distance. A queue is used to implement breadth-first search. It
	 * contains vertices whose distance from the source node has been computed
	 * and their adjacent vertices are to be examined. As an example, consider
	 * the following graph and its adjacency list representation. Directed
	 * 
	 */
	public static long[] shortestPathUnweighted(Graph g, int source) {
		Queue<Vertex> queue = new LinkedList<Vertex>();
		queue.add(g.getVertex(source));
		long[] distance = new long[g.getV()];
		for (int i = 0; i < g.getV(); i++) {
			g.getVertex(i).setDistance(-1);
			distance[i] = -1;
		}
		g.getVertex(source).setDistance(0);
		while (!queue.isEmpty()) {
			Vertex u = queue.remove();
			List<Edge> adj = u.getAdjacent();
			if (adj == null || adj.isEmpty()) {
				continue;
			}
			for (Edge v : adj) {
				long distancex = (long) (u.getDistance() + 1);
				if (v.getIncident().getDistance() == -1 || v.getIncident().getDistance() > distancex) {
					g.getVertex(v.getIncident().getIndex()).setDistance(distancex);
					queue.add(g.getVertex(v.getIncident().getIndex()));
					distance[v.getIncident().getIndex()] = distancex;
				}
			}
		}
		return distance;
	}

	public static long[] singleSourceOccurances(long[] input) {
		long[] response = new long[input.length];
		for (int i = 0; i < input.length; i++) {
			response[(int) input[i]]++;
		}
		return response;
	}
}
