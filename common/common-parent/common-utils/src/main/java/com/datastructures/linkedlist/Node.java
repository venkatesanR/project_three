package com.datastructures.linkedlist;

public class Node<T> {
	private T data;
	private Node next;
	private Node prev;

	public Node(T data, Node next) {
		this.data = data;
		this.next = next;
	}

	public Node(T data, Node next, Node prev) {
		this.data = data;
		this.next = next;
		this.prev = prev;
	}


	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Node getNext() {
		return next;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public Node getPrev() {
		return prev;
	}

	public void setPrev(Node prev) {
		this.prev = prev;
	}

}
