package com.datastructures.linkedlist;

public class SinglyLinkedList<T> implements LinkedListADT<T> {
	private Node head;

	@Override
	public void add(Object data, int position) {
		int k = 0;
		if (position == 0) {
			Node tempHead = head;
			head = new Node<T>((T) data, tempHead);
		} else {
			Node current = null;
			Node oldNext = null;
			while (k < position) {
				current = head.getNext();
				if (current != null) {
					oldNext = current.getNext();
				}
				k++;
			}
			if (current != null) {
				oldNext = current.getNext();
				current.setNext(new Node<T>((T) data, oldNext));
			} else {
				current = new Node<T>((T) data, oldNext);
			}
		}

	}

	@Override
	public int size() {
		int count = 0;
		Node current = head;
		while (current != null) {
			count += 1;
			current = current.getNext();
			if (current == null)
				break;
		}
		return count;
	}

	@Override
	public void add(Object data) {
		add(data, size());
	}

	public static void main(String[] args) {
		SinglyLinkedList<String> list = new SinglyLinkedList<>();
		list.add("First");
		list.add("Second");
		System.out.println(list.size());
	}

}
