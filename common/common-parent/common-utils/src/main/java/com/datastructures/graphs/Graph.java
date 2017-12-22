package com.datastructures.graphs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.commons.io.FileUtils;

/**
 * Adjacency lists are generally preferred because they efficiently represent
 * sparse graphs. An adjacency matrix is preferred if the graph is dense, that
 * is the number of edges |E | is close to the number of vertices squared,
 * |V|2,<br>
 * or if one must be able to quickly look up if there is an edge connecting two
 * vertices.
 * 
 * @author vrengasamy
 *
 */
public class Graph<E> {
	private static final int DEFAULT_VERTEX = 10;
	private int V;
	private int E;
	private boolean directed;
	private Vertex[] vertexs;
	private int lastVertex = 0;

	public Graph() {
		this.V = DEFAULT_VERTEX;
		this.directed = false;
		vertexs = new Vertex[DEFAULT_VERTEX];
	}

	public Graph(int v) {
		this.V = v;
		this.directed = false;
		vertexs = new Vertex[v];
	}

	public Graph(int v, boolean directed) {
		this.V = v;
		this.directed = directed;
		vertexs = new Vertex[v];
	}

	public void addVertex(E x) throws InvalidActivityException {
		if (lastVertex > V) {
			throw new InvalidActivityException("You cannot add more entry than specified vertex count");
		}
		Vertex<E> v = new Vertex<E>(x);
		v.setData(x);
		v.setIndex(lastVertex);
		vertexs[lastVertex] = v;
		lastVertex += 1;
	}

	public void removeVertex(int x) {

	}

	public void addEdge(int x, int y) {
		if (directed) {
			if (vertexs[x].getAdjacent() == null) {
				vertexs[x].setAdjacent(new ArrayList<>());
			}
			vertexs[x].getAdjacent().add(getEdge(vertexs[y]));
		} else {
			if (vertexs[x].getAdjacent() == null) {
				vertexs[x].setAdjacent(new ArrayList<>());
			}
			if (vertexs[y].getAdjacent() == null) {
				vertexs[y].setAdjacent(new ArrayList<>());
			}
			vertexs[x].getAdjacent().add(getEdge(vertexs[y]));
			vertexs[y].getAdjacent().add(getEdge(vertexs[x]));
		}
		E++;
	}

	public boolean removeEdge(int x, int y) {
		return false;
	}

	public boolean adjacent(int x, int y) {
		return false;
	}

	public Collection<Vertex> neigbours(int x) {
		return this.vertexs[x].getAdjacent();
	}

	public E getVertexValue(int x) {
		return null;
	}

	public void setVertexValue(int x, E v) {

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < V; i++) {
			Vertex v = this.vertexs[i];
			if (v != null) {
				builder.append("V" + v.getIndex());
				List<Edge> adj = v.getAdjacent();
				if (adj == null || adj.isEmpty()) {
					continue;
				}
				for (int j = 0; j < adj.size(); j++) {
					builder.append("-E" + adj.get(j).getIncident().getIndex());
				}
				builder.append("\n");
			}

		}
		return builder.toString();
	}

	public int degree(int x) {
		return this.vertexs[x].getAdjacent() != null ? this.vertexs[x].getAdjacent().size() : 0;
	}

	public int maxDegree() {
		int max = 0;
		for (int i = 0; i < V; i++) {
			int newDeg = degree(i);
			if (newDeg > max)
				max = newDeg;
		}
		return max;
	}

	public int avgDegree() {
		return 2 * E / V;
	}

	public int selfLoops() {
		int count = 0;
		for (int i = 0; i < V; i++) {
			List<Edge> adj = this.vertexs[i].getAdjacent();
			if (adj == null || adj.isEmpty()) {
				continue;
			}
			for (int j = 0; j < adj.size(); j++) {
				if (adj.get(j).getIncident().equals(this.vertexs[i])) {
					count += 1;
				}
			}
		}
		return count / 2;
	}

	private Edge getEdge(Vertex y) {
		return new Edge(y);
	}

	private Edge getEdge(Vertex y, int weight) {
		return new Edge(y, weight);
	}
}
