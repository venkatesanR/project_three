package com.datastructures.linkedlist;

public class DoublyLinkedList<T> extends LinkedListADT<T> {

	/**
	 * Similar to singly-linked list we can add data into doubly linked list
	 * problem here was we should have to taken care of two(forward,backward
	 * pointers)
	 */
	@Override
	public void add(Object data, int position) {
		if (head == null) {
			head = new Node<T>((T) data, null, null);
		} else if (position == 0) {
			Node temp = head;
			head = new Node<T>((T) data, temp, null);
		} else {
			int k = 0;
			Node p = head;
			Node cp = null;
			while (p != null && k < position) {
				cp = p;
				p = p.getNext();
			}
			cp.setNext(new Node<T>((T) data, p, cp));
		}
		count++;
	}

	/**
	 * just like SLS it will add end of the list
	 */
	@Override
	public void add(Object data) {
		add(data, size());
	}

	@Override
	public void delete(int position) {

	}

	@Override
	public void delete() {

	}

	@Override
	public void flush() {

	}

	@Override
	public String toString() {
		int tabCount = 0;
		StringBuilder builder = new StringBuilder();
		Node iterate = head;
		while (iterate != null) {
			builder.append("(").append(iterate.getData()).append("\t")
					.append(iterate.getNext() != null ? iterate.getNext().getData() : "NULL").append(")").append("\n");
			builder.append("(").append(iterate.getData()).append("\t")
					.append(iterate.getPrev() != null ? iterate.getPrev().getData() : "NULL").append(")").append("\n");
			iterate = iterate.getNext();
			tabCount++;
		}
		return builder.toString();
	}

}
