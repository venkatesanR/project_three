package com.techmania.datastructures;

import java.util.Set;

public class Node {
	private Integer id;
	private Set<Integer> linked;

	public Node(int id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public Set<Integer> getLinked() {
		return linked;
	}

	public void setLinked(Set<Integer> linked) {
		this.linked = linked;
	}

}
