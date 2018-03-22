package com.datastructures.linkedlist;

public class DriverClass {
	public static void main(String[] args) {
		SinglyLinkedList<String> list = new SinglyLinkedList<>();
		LinkedListUtil.getList(list);
		Node node=LinkedListUtil.reverse(list.head);
		LinkedListUtil.print(node);
	}
}
