package com.datastructures.trees;

public interface Tree {
	public void insert(BTreeNode node);

	public void delete();

	public BTreeNode search(int levelIndex);

	public void traverse();
	
	public int size();
	
	public int height();
	
}
