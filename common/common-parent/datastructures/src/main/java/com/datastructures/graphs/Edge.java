package com.datastructures.graphs;

public class Edge<T extends Number> {
	private Vertex incident;
	private T weight;

	public Edge(Vertex incident) {
		this.incident = incident;
	}

	public Edge(Vertex incident, T weight) {
		this.incident = incident;
		this.weight = weight;
	}

	public Vertex getIncident() {
		return incident;
	}

	public void setIncident(Vertex incident) {
		this.incident = incident;
	}

	public T getWeight() {
		return weight;
	}

	public void setWeight(T weight) {
		this.weight = weight;
	}

}
