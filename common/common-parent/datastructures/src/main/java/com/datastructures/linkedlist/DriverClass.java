package com.datastructures.linkedlist;

import com.datastructures.models.LinkedNode;

public class DriverClass {
    public static void main(String[] args) {
        SinglyLinkedList<String> list = new SinglyLinkedList<>();
        LinkedListUtil.getList(list);
        LinkedNode node = LinkedListUtil.reverse(list.head);
        LinkedListUtil.print(LinkedListUtil.reverse(node));
    }
}
