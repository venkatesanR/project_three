package com.datastructures.graphs;

interface IGraph {
	public enum TRAVERSE {
		DFS("DFS"), BFS("BFS");
		public String description;

		TRAVERSE(String description) {
			this.description = description;
		}
	}

}
