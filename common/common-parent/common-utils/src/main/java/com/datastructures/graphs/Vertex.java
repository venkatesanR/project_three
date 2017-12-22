package com.datastructures.graphs;

import java.util.List;

public class Vertex<E> {
	private E data;
	private List<Edge> adjacent;
	private int index;
	
	public Vertex(E data) {
		this.data = data;
	}

	public E getData() {
		return data;
	}

	public void setData(E data) {
		this.data = data;
	}

	public List<Edge> getAdjacent() {
		return adjacent;
	}

	public void setAdjacent(List<Edge> adjacent) {
		this.adjacent = adjacent;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
