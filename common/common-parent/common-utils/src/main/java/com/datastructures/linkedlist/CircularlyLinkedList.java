package com.datastructures.linkedlist;

import com.datastructures.utils.LinkedListUtil;

/**
 * 
 * @author vrengasamy
 *
 * @param <T>
 */
public class CircularlyLinkedList<T> extends LinkedListADT<T> {
	protected int count;

	protected Node head;

	@Override
	public void add(T data, int position) {
		int k = 0;
		if (head == null) {
			head = new Node<T>(data, head);
			head.setNext(head);
		} else if (position == 0) {
			Node tempHead = head;
			head = new Node<T>(data, tempHead);
			Node tail = findLast();
			if (tail != null) {
				tail.setNext(head);
			}
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
				q = new Node<T>((T) data, head);
			}
		}
		count++;
	}

	@Override
	public void add(T data) {
		add(data, size());
	}

	@Override
	public void delete(int position) {
		int k = 0;
		if (position == 0 && head != null) {
			Node trashNode = head;
			head = head.getNext();
			Node tail = findLast();
			if(tail!=null) {
				tail.setNext(head);
			}
			count--;
			trashNode=null;
		} else if (head != null) {
			Node p = head;
			Node pre = null;
			while (k < position && p != null) {
				pre = p;
				p = p.getNext();
				k++;
			}
			if (pre != null) {
				pre.setNext(p != head ? p.getNext() : head);
			}
			p = null;
			count--;
		}
	}

	@Override
	public void delete() {
		delete(size());
	}

	@Override
	public void flush() {

	}

	@Override
	public int size() {
		return count;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Node current = head;
		do {
			builder.append(current.getData()).append(",");
			current = current.getNext();
		} while (!current.equals(head));
		return builder.deleteCharAt(builder.length() - 1).toString();
	}
	
	public boolean isCircular() {
		return LinkedListUtil.loopExists(head);
	}
	//utility calls
	private Node findLast() {
		Node lastNode = head;
		while (!lastNode.getNext().equals(head)) {
			lastNode = lastNode.getNext();
		}
		return lastNode;
	}

}
