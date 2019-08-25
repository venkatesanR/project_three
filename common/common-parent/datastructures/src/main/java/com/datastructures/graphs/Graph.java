package com.datastructures.graphs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class Graph<E, T extends Number> {
	private int V;
	private int E;
	private boolean directed;
	private Vertex[] vertexs;
	private long[][] path = null;
	private int index = 0;

	public Graph(int v) {
		this.V = v;
		this.directed = false;
		vertexs = new Vertex[v];
		path = new long[v][v];
	}

	public Graph(int v, boolean directed) {
		this.V = v;
		this.directed = directed;
		vertexs = new Vertex[v];
		path = new long[v][v];
	}

	public int getV() {
		return V;
	}

	public int getE() {
		return E;
	}

	public boolean isDirected() {
		return directed;
	}

	public void addVertex(E x) {
		Vertex<E> v = new Vertex<E>(x);
		v.setIndex(index);
		vertexs[index] = v;
		index += 1;
	}

	public void addEdge(int x, int y, T cost, boolean keepSmallest) {
		if (directed) {
			if (vertexs[x].getAdjacent() == null) {
				vertexs[x].setAdjacent(new ArrayList<>());
			}
			path[x][y] = cost.longValue();
			vertexs[x].getAdjacent().add(getEdge(vertexs[y], cost));
		} else {
			if (vertexs[x].getAdjacent() == null) {
				vertexs[x].setAdjacent(new ArrayList<>());
			}
			if (vertexs[y].getAdjacent() == null) {
				vertexs[y].setAdjacent(new ArrayList<>());
			}
			if (!keepSmallest || (keepSmallest && (path[x][y] == 0 || path[x][y] > cost.longValue()))) {
				path[x][y] = cost.longValue();
				path[y][x] = cost.longValue();
				vertexs[x].getAdjacent().add(getEdge(vertexs[y], cost));
				vertexs[y].getAdjacent().add(getEdge(vertexs[x], cost));
			}
		}
		E++;
	}

	public Collection<Edge> neigbours(int x) {
		return this.vertexs[x].getAdjacent();
	}

	public Vertex getVertex(int x) {
		return this.vertexs[x];
	}

	public E getVertexValue(int x) {
		return (E) this.vertexs[x].getData();
	}

	private Edge getEdge(Vertex y) {
		return new Edge(y);
	}

	private Edge getEdge(Vertex y, T weight) {
		return new Edge(y, weight);
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
}
