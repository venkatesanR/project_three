package com.techmania.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph {
	private int vertex;
	private int edge;
	private List[] adj;
	private Map<String, Long> cachedPaths;
	private Integer[] marked;
	private long distance = 0;
	private boolean enableRCR1 = true;
	private boolean enableRCR2 = true;
	private Integer rootNode;
	private List<String> path;
	private Set<Integer> bulkChild;

	public Graph(int numVertex) {
		cachedPaths = new HashMap<>();
		this.vertex = numVertex;
		adj = new List[numVertex];
		marked = new Integer[numVertex];
		bulkChild = new HashSet<>();
	}

	public Integer V() {
		return vertex;
	}

	public Integer E() {
		return edge;
	}

	public void init() {
		enableRCR1 = true;
		enableRCR2 = true;
		rootNode = null;
		distance = 0;
		path = new ArrayList<>();
	}

	public void addEdge(int s, int t) {
		distance = 1;
		int a = s - 1;
		int b = t - 1;
		if (adj[a] == null) {
			adj[a] = new ArrayList<>();
		}
		if (adj[b] == null) {
			adj[b] = new ArrayList<>();
		}
		adj[a].add(a);
		adj[b].add(b);
		if (adj[a].size() > 1) {
			bulkChild.add(a);
		}
		if (adj[b].size() > 1) {
			bulkChild.add(b);
		}
		edge++;
		String fKey = String.valueOf(s).concat(String.valueOf(t));
		cachedPaths.put(fKey, distance);
		distance = 0;
	}

	public void prepareLongestPath() {
		
	}

	public List<Integer> adj(int v) {
		return adj[v];
	}

	public long distance(int s, int d) {
		init();
		String fKey = String.valueOf(s).concat(String.valueOf(d));
		if (cachedPaths.get(fKey) != null)
			return cachedPaths.get(fKey);
		String rKey = String.valueOf(d).concat(String.valueOf(s));
		if (cachedPaths.get(rKey) != null)
			return cachedPaths.get(rKey);
		findPath(s, d);
		factDist(0, path);
		cachedPaths.put(fKey, distance);
		return distance;
	}

	private void factDist(int start, List<String> data) {
		if (start < data.size()) {
			int destVal = 0;
			for (int index = start; index < data.size(); index++) {
				if (index + 1 < data.size()) {
					String fKey = String.valueOf(data.get(start)).concat(data.get(index + 1));
					destVal = destVal + 1;
					cachedPaths.put(fKey, Long.valueOf(destVal));
				}
			}
			factDist(start + 1, data);
		}
	}

	private void findPath(int s, int d) {
		if (s == d) {
			enableRCR1 = false;
		}
		if (enableRCR1) {
			rootNode = null;
			enableRCR2 = true;
			marked = new Integer[V()];
			findRootNode(s, d);
			if (rootNode != null) {
				distance += 1;
				path.add(String.valueOf(rootNode.intValue()));
				Integer key = prepareKey(String.valueOf(s), String.valueOf(rootNode.intValue()));
				Long cachedDist = cachedPaths.get(key);
				if (cachedDist != null) {
					distance += cachedDist;
					enableRCR1 = false;
					enableRCR2 = false;
					path.clear();
				}
				findPath(s, rootNode.intValue());
			}
		}
	}

	private void findRootNode(int node, int match) {
		List<Integer> a1 = adj(node);
		if (enableRCR2 && (marked[node] == null || !marked[node].equals(1))) {
			marked[node] = 1;
			if (a1 != null && a1.contains(match)) {
				enableRCR2 = false;
				rootNode = node;
			}
			if (a1 != null && !a1.isEmpty()) {
				for (Integer data : a1) {
					findRootNode(data.intValue(), match);
				}
			}
		}
	}

	private Integer prepareKey(String source, String dest) {
		return source.hashCode() ^ dest.hashCode();
	}
}