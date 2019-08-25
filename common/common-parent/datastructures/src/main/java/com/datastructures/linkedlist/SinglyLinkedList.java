package com.datastructures.linkedlist;

import com.datastructures.models.LinkedNode;

public class SinglyLinkedList<Type> implements LinkedListADT<Type> {
    protected int count;

    protected LinkedNode<Type> head;

    @Override
    public void add(Type data, int position) {
        int k = 0;
        if (head == null) {
            head = new LinkedNode<Type>(data, null);
        } else if (position == 0) {
            LinkedNode tempHead = head;
            head = new LinkedNode<Type>(data, tempHead);
        } else {
            LinkedNode p = head;
            LinkedNode q = null;
            while (k < position && p != null) {
                q = p;
                p = p.getNext();
                k++;
            }
            if (q != null) {
                q.setNext(new LinkedNode<Type>((Type) data, p));
            } else {
                q = new LinkedNode<Type>((Type) data, null);
            }
        }
        count++;
    }

    @Override
    public int size() {
        return count;
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

    public LinkedNode removeAll(LinkedNode node, int n) {/*
		Node currNode = head;
		while (currNode != null) {
			if (currNode.getData() == n) {
				currNode.next = currNode.next.next;
			}
			currNode = currNode.next;
		}
		return head;

	*/
        return null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        LinkedNode iterate = head;
        while (iterate != null) {
            builder.append(iterate.getData()).append("-->");
            iterate = iterate.getNext();
        }
        return builder.append("NULL").toString();
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
}
