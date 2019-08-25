package com.datastructures.graphs;

import java.util.List;

public class Vertex<E> implements Comparable<Vertex<E>> {
	private E data;
	private List<Edge> adjacent;
	private int index;
	private double distance;

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

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	@Override
	public int compareTo(Vertex<E> o) {
		if (o.getDistance() > this.distance) {
			return -1;
		} else if (o.getDistance() < this.distance) {
			return 1;
		}
		return 0;
	}
}
