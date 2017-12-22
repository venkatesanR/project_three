package com.datastructures.graphs;

public class Edge {
	private Vertex incident;
	private int weight;

	public Edge(Vertex incident) {
		this.incident = incident;
	}

	public Edge(Vertex incident, int weight) {
		this.incident = incident;
		this.weight = weight;
	}

	public Vertex getIncident() {
		return incident;
	}

	public void setIncident(Vertex incident) {
		this.incident = incident;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}


}
