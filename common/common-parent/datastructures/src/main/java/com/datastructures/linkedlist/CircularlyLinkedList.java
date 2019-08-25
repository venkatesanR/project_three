package com.datastructures.linkedlist;

import com.datastructures.models.LinkedNode;

/**
 * @param <T>
 * @author vrengasamy
 */
public class CircularlyLinkedList<Type> implements LinkedListADT<Type> {
    protected int count;

    protected LinkedNode<Type> head;

    @Override
    public void add(Type data, int position) {
        int k = 0;
        if (head == null) {
            head = new LinkedNode(data, head);
            head.setNext(head);
        } else if (position == 0) {
            LinkedNode tempHead = head;
            head = new LinkedNode(data, tempHead);
            LinkedNode tail = findLast();
            if (tail != null) {
                tail.setNext(head);
            }
        } else {
            LinkedNode p = head;
            LinkedNode q = null;
            while (k < position && p != null) {
                q = p;
                p = p.getNext();
                k++;
            }
            if (q != null) {
                q.setNext(new LinkedNode(data, p));
            } else {
                q = new LinkedNode(data, head);
            }
        }
        count++;
    }

    @Override
    public void add(Type data) {
        add(data, size());
    }

    @Override
    public void delete(int position) {
        int k = 0;
        if (position == 0 && head != null) {
            LinkedNode trashNode = head;
            head = head.getNext();
            LinkedNode tail = findLast();
            if (tail != null) {
                tail.setNext(head);
            }
            count--;
            trashNode = null;
        } else if (head != null) {
            LinkedNode p = head;
            LinkedNode pre = null;
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
        LinkedNode current = head;
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
    private LinkedNode findLast() {
        LinkedNode lastNode = head;
        while (!lastNode.getNext().equals(head)) {
            lastNode = lastNode.getNext();
        }
        return lastNode;
    }

}
