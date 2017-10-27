package com.datastructures.linkedlist;

public class DriverClass {
	public static void main(String[] args) {
		SinglyLinkedList<String> list = new SinglyLinkedList<>();
		DoublyLinkedList<String> dlist = new DoublyLinkedList<>();
		getList(dlist);
		System.out.println(list.toString());
	}

	private static <T extends LinkedListADT> void getList(T list) {
		list.add("First");
		list.add("Second");
		list.add("Third");
		list.add("Fourth");
		// list.add("Medium", 2);
		list.delete(3);
		list.add("Fourth");
		System.out.println(list.toString());
		list.add("Fifth");
	}
}
