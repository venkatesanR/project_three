package com.techmania.datastructures;

public class Edge<W> {
	private W weight;
	private Node firstNode;
	private Node secondNode;

	public W getWeight() {
		return weight;
	}

	public void setWeight(W weight) {
		this.weight = weight;
	}

	public Node getFirstNode() {
		return firstNode;
	}

	public void setFirstNode(Node firstNode) {
		this.firstNode = firstNode;
	}

	public Node getSecondNode() {
		return secondNode;
	}

	public void setSecondNode(Node secondNode) {
		this.secondNode = secondNode;
	}

}
