package com.datastructures.trees;

public class BTreeNode<T> {
	private T data;
	private BTreeNode left;
	private BTreeNode right;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public BTreeNode getLeft() {
		return left;
	}

	public void setLeft(BTreeNode left) {
		this.left = left;
	}

	public BTreeNode getRight() {
		return right;
	}

	public void setRight(BTreeNode right) {
		this.right = right;
	}

}
