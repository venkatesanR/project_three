package com.datastructures.linkedlist;

public class SinglyLinkedList<T> extends LinkedListADT<T> {
	protected int count;

	protected Node head;

	@Override
	public void add(Object data, int position) {
		int k = 0;
		if (head == null) {
			head = new Node<T>((T) data, null);
		} else if (position == 0) {
			Node tempHead = head;
			head = new Node<T>((T) data, tempHead);
		} else {
			Node p = head;
			Node q = null;
			while (k < position && p != null) {
				q = p;
				p = p.getNext();
				k++;
			}
			if (q != null) {
				q.setNext(new Node<T>((T) data, p));
			} else {
				q = new Node<T>((T) data, null);
			}
		}
		count++;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public void add(Object data) {
		add(data, size());
	}

	@Override
	public void delete(int position) {
		int k = 0;
		if (position == 0 && head != null) {
			Node trashNode = head;
			head = head.getNext();
			count--;
		} else if (head != null) {
			Node p = head;
			Node pre = null;
			while (k < position && p != null) {
				pre = p;
				p = p.getNext();
				k++;
			}
			if (pre != null) {
				pre.setNext(p != null ? p.getNext() : null);
			}
			p = null;
			count--;
		}
	}

	@Override
	public void delete() {
		delete(size());
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		Node iterate = head;
		while (iterate != null) {
			builder.append(iterate.getData()).append("-->");
			iterate = iterate.getNext();
		}
		return builder.append("NULL").toString();
	}

	@Override
	public void flush() {
		Node pointer = head;
		Node flushP = null;
		while (pointer != null) {
			flushP = pointer.getNext();
			pointer = null;
			pointer = flushP;
		}
	}
}
