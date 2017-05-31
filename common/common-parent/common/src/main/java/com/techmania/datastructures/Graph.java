package com.techmania.datastructures;

public class Graph {
	private Node[] nodes;

	public Graph(int size) {
		nodes = new Node[size];
		for (int index = 0; index < size; index++) {
			addNode(index, getNewNode());
		}
	}

	public Node getNewNode() {
		return new Node();
	}

	public void addNode(int index, Node node) {
		node.setData(index + 1);
		nodes[index] = node;
	}

	public void getDistance(Node from, Node to,boolean isWeightBased) {
		if(from!=null && from.) {
			
		}
	}
}
