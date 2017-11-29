package com.datastructures.linkedlist;

public class DriverClass {
	public static void main(String[] args) {
		//SinglyLinkedList<String> list = new SinglyLinkedList<>();
		//DoublyLinkedList<String> dlist = new DoublyLinkedList<>();
		CircularlyLinkedList<String> clist = new CircularlyLinkedList<>();
		getList(clist);
		System.out.println(clist.toString());
		System.out.println("IS_CIRCULAR :"+clist.isCircular());
	}

	private static void getList(LinkedListADT list) {
		list.add("First");
		list.add("Second");
		list.add("Third");
		list.add("Fourth");
		list.add("Fifth");
	}
}
