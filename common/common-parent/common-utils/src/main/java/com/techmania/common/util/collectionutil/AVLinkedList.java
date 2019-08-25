/*
 * AVLinkedList.java
 *
 * Created on October 30, 2006, 4:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.techmania.common.util.collectionutil;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This linked list is different from the java.util implementation in that it exposes access to
 * the nodes themselves, allowing lower level manipulation.  This ends up being rather critical
 * when removing elements from a cache.  Having a reference to the node allows it to be removed
 * in constant time - rather than having to search for it first.
 */
public class AVLinkedList {
    private LinkedListNode header = null;
    private int size = 0;
    protected Lock listLock = null;

    /**
     * Constructor AVLinkedList
     */
    public AVLinkedList() {

        header = new LinkedListNode(null);
        header.value = header;    // this is how I designate the header node
        header.prev = header;
        header.next = header;
        listLock = new ReentrantLock();
    }


    /**
     * adding an object to the list, making it the new first node.
     * for the purposes of treating this list as a queue or stack, <b>this</b>
     * is the end of the list that should be used when adding.
     */
    public final LinkedListNode addFirst(Object obj) {
        return addBefore(header.next, obj);
    }


    /**
     * adding an object to the list, making it the new last node.
     */
    public final LinkedListNode addLast(Object obj) {
        return addBefore(header, obj);
    }


    /**
     * remove a node from the end of the list (list is being used like a <b>queue</b>).
     */
    public final Object dequeue() {
        return removeLast();
    }


    /**
     * remove a node from the beginning of the list (list is being
     * used like a <b>stack</b>)
     */
    public final Object pop() {
        return removeFirst();
    }


    /**
     * peek the first element without removing it.  This is a <b>stack</b> style peek
     */
    public final LinkedListNode peekFirst() {

        return (header.next == header)
                ? null
                : header.next;
    }


    /**
     * peek the last element without removing it.  This is a <b>queue</b> style peek
     */
    public final LinkedListNode peekLast() {

        return (header.prev == header)
                ? null
                : header.prev;
    }


    /**
     * Method removeFirst
     */
    private final Object removeFirst() {
        return remove(header.next);
    }


    /**
     * Method removeLast
     */
    private final Object removeLast() {
        return remove(header.prev);
    }


    /**
     * promotes this node to the front of the the list.
     */
    public final void moveToFirst(LinkedListNode node) {
        //if already first, don't do anything
        if (node.getPrevious() == null || node.getPrevious().isHeaderNode()) {
            return;
        }
        remove(node);
        addNodeBefore(header.next, node);
    }


    /**
     * demotes this node to the end of the list.
     */
    public final void moveToLast(LinkedListNode node) {
        remove(node);
        addNodeBefore(header, node);
    }


    /**
     * Method addBefore (somewhat expensive - alloc)
     */
    public final LinkedListNode addBefore(LinkedListNode node, Object obj) {

        LinkedListNode newNode = new LinkedListNode(obj);

        addNodeBefore(node, newNode);

        return newNode;
    }


    /**
     * Method addNodeBefore
     */
    public final void addNodeBefore(LinkedListNode nodeToAddBefore, LinkedListNode newPreviousNode) {

        newPreviousNode.prev = nodeToAddBefore.prev;
        newPreviousNode.next = nodeToAddBefore;
        newPreviousNode.prev.next = newPreviousNode;
        newPreviousNode.next.prev = newPreviousNode;

        size++;
    }


    /**
     * Removes the node from the list and returns the value of the former node.
     */
    public final Object remove(LinkedListNode node) {

        if ((node == null) || (node == header)) {
            return null;
        }

        node.prev.next = node.next;
        node.next.prev = node.prev;
        node.prev = null;
        node.next = null;

        size--;

        return node.value;
    }


    /**
     * Method size
     */
    public int size() {
        return size;
    }


    /**
     * Method toString
     */
    public String toString() {

        StringBuffer sb = new StringBuffer();
        LinkedListNode thisNode = header.next;

        sb.append("[LIST size=" + size() + "]");

        while (thisNode != header) {
            sb.append("<->");
            sb.append(String.valueOf(thisNode.value));

            thisNode = thisNode.next;
        }

        return sb.toString();
    }

    public boolean lock(long timeOutInMillis) throws InterruptedException {
        return listLock.tryLock(timeOutInMillis, TimeUnit.MILLISECONDS);
    }

    public void unlock() {
        listLock.unlock();
    }
}
