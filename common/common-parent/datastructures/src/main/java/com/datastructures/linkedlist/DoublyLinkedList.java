package com.datastructures.linkedlist;

import com.datastructures.models.LinkedNode;

public class DoublyLinkedList<T> implements LinkedListADT<T> {
    protected int count;

    protected LinkedNode<T> head;

    @Override
    public int size() {
        return count;
    }

    /**
     * Similar to singly-linked list we can add data into doubly linked list
     * problem here was we should have to taken care of two(forward,backward
     * pointers)
     */
    @Override
    public void add(T data, int position) {
        if (head == null) {
            head = new LinkedNode(data, null, null);
        } else if (position == 0) {
            LinkedNode temp = head;
            head = new LinkedNode(data, temp, null);
        } else {
            int k = 0;
            LinkedNode p = head;
            LinkedNode cp = null;
            while (p != null && k < position) {
                cp = p;
                p = p.getNext();
            }
            cp.setNext(new LinkedNode(data, p, cp));
        }
        count++;
    }

    /**
     * just like SLS it will add end of the list
     */
    @Override
    public void add(T data) {
        add(data, size());
    }

    @Override
    public void delete(int position) {
        int k = 0;
        if (position == 0 && head != null) {
            LinkedNode trashNode = head;
            head = head.getNext();
            count--;
        } else if (head != null) {
            LinkedNode p = head;
            LinkedNode pre = null;
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

    @Override
    public void flush() {
        LinkedNode pointer = head;
        LinkedNode flushP = null;
        while (pointer != null) {
            flushP = pointer.getNext();
            pointer = null;
            pointer = flushP;
        }
    }

    @Override
    public String toString() {
        int tabCount = 0;
        StringBuilder builder = new StringBuilder();
        LinkedNode iterate = head;
        while (iterate != null) {
            builder.append("(").append(iterate.getData()).append("\t")
                    .append(iterate.getNext() != null ? iterate.getNext().getData() : "NULL").append(")").append("\n");
            builder.append("(").append(iterate.getData()).append("\t")
                    .append(iterate.getPrev() != null ? iterate.getPrev().getData() : "NULL").append(")").append("\n");
            iterate = iterate.getNext();
            tabCount++;
        }
        return builder.toString();
    }

}
