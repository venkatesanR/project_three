package com.datastructures.utils;

import com.datastructures.linkedlist.LinkedListADT;
import com.datastructures.linkedlist.Node;

public class LinkedListUtil {
	private LinkedListUtil() {
	}

	public static boolean loopExists(Node head) {
		boolean loop = false;
		Node slowPtr = head;
		Node fastPtr = head;
		while (slowPtr != null && fastPtr != null && fastPtr.getNext() != null) {
			slowPtr = slowPtr.getNext();
			fastPtr = fastPtr.getNext().getNext();
			if (slowPtr.equals(fastPtr)) {
				loop = true;
				break;
			}
		}
		return loop;
	}

	public static void getList(LinkedListADT list) {
		list.add("First");
		list.add("Second");
		list.add("Third");
		list.add("Fourth");
		list.add("Fifth");
	}

	public static Node reverse(Node head) {
		Node current = head;
		Node previous = null;
		while (current != null) {
			Node temp = current.getNext();
			current.setNext(previous);
			previous = current;
			current = temp;
		}
		return previous;
	}

	public static void print(Node iterate) {
		StringBuilder builder = new StringBuilder();
		while (iterate != null) {
			builder.append(iterate.getData()).append("-->");
			iterate = iterate.getNext();
		}
		System.out.println(builder.append("NULL").toString());
	}
}
