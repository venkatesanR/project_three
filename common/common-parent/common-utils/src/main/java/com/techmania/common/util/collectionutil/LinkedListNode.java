/*
 * LinkedListNode.java
 *
 * Created on October 30, 2006, 4:14 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.techmania.common.util.collectionutil;

/**
 *
 */
public class LinkedListNode {
    LinkedListNode next;
    LinkedListNode prev;
    Object value;

    public LinkedListNode(Object value) {
        this.value = value;
    }


    /**
     * Method getValue
     */
    public Object getValue() {
        return value;
    }


    /**
     * Method getNext
     */
    public LinkedListNode getNext() {

        return (next.isHeaderNode()
                ? null
                : next);
    }


    /**
     * Method getPrevious
     */
    public LinkedListNode getPrevious() {

        return (prev.isHeaderNode()
                ? null
                : prev);
    }


    /**
     * is this node the header node in a linked list?
     */
    boolean isHeaderNode() {
        return (value == this);
    }

}
