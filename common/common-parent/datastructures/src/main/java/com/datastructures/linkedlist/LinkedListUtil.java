package com.datastructures.linkedlist;


import com.datastructures.models.LinkedNode;

public class LinkedListUtil {

    private LinkedListUtil() {
    }

    public static boolean loopExists(LinkedNode head) {
        boolean loop = false;
        LinkedNode slowPtr = head;
        LinkedNode fastPtr = head;
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

    public static LinkedNode reverse(LinkedNode head) {
        LinkedNode current = head;
        LinkedNode previous = null;
        while (current != null) {
            LinkedNode temp = current.getNext();
            current.setNext(previous);
            previous = current;
            current = temp;
        }
        return previous;
    }

    public static void print(LinkedNode iterate) {
        StringBuilder builder = new StringBuilder();
        while (iterate != null) {
            builder.append(iterate.getData()).append("-->");
            iterate = iterate.getNext();
        }
        System.out.println(builder.append("NULL").toString());
    }

}
