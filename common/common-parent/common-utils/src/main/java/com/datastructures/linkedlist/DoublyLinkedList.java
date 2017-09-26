package com.datastructures.linkedlist;

public class DoublyLinkedList<T> extends LinkedListADT<T> {

	/**
	 * simliar to singly-linked list we can add data into doubly linked list
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
			Node c = null;
			while (p != null && k < position) {
				c = p;
				p = p.getNext();
			}
		}
	}

	@Override
	public void add(Object data) {

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

}
