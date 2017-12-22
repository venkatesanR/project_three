package com.datastructures.graphs;

public class Edge {
	private Vertex x;
	private Vertex y;
	private int weight;

	public Edge(Vertex x, Vertex y) {
		this.x = x;
		this.y = y;
	}

	public Edge(Vertex x, Vertex y, int weight) {
		this.x = x;
		this.y = y;
		this.weight = weight;
	}

	public Vertex getX() {
		return x;
	}

	public void setX(Vertex x) {
		this.x = x;
	}

	public Vertex getY() {
		return y;
	}

	public void setY(Vertex y) {
		this.y = y;
	}

}
