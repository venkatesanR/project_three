package com.datastructures.utils;

import org.springframework.jmx.access.InvalidInvocationException;

import com.datastructures.linkedlist.Node;

public class LinkedListUtil {
	private LinkedListUtil() {
		throw new InvalidInvocationException("No instance for you");
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
				return loop;
			}
		}
		return loop;
	}
}
