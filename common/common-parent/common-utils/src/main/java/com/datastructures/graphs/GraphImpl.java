package com.datastructures.graphs;

import java.util.LinkedList;

public class GraphImpl {
	private int vertex;
	private int edge;
	private LinkedList<Edge>[] adjacencyList;
	private int[] marked;

	public Integer V() {
		return vertex;
	}

	public Integer E() {
		return edge;
	}
	
	public GraphImpl(int vertex) {
		this.vertex = vertex;
		marked = new int[vertex];
		adjacencyList = new LinkedList[vertex];
	}
	
	public void addEdge(int s, int t) {
		// assuming bi directional
		if (adjacencyList[s] == null) {
			adjacencyList[s] = new LinkedList<>();
		}
		adjacencyList[s].add(t);
		edge++;
	}

	
}
