package com.techmania.datastructures;

public class Node<T,W> {
	private Edge[] edges;
	private T data;
	
	public Edge[] getEdges() {
		return edges;
	}
	public void setEdges(Edge[] edges) {
		this.edges = edges;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
