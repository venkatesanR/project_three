package com.datastructures.queue;

import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {
    private int count = 0;
    private Node<Item> queue;

    // construct an empty deque
    public Deque() {

    }

    // is the deque empty?
    public boolean isEmpty() {
        return queue == null;
    }

    // return the number of items on the deque
    public int size() {
        return count;
    }

    // add the item to the front
    public void addFirst(Item item) {
        throwIfNull(item);
        add(true, item);
    }

    // add the item to the back
    public void addLast(Item item) {
        throwIfNull(item);
        add(false, item);
    }

    // remove and return the item from the front
    public Item removeFirst() {
        return remove(true);
    }


    // remove and return the item from the back
    public Item removeLast() {
        return remove(false);
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private Node<Item> current = queue;

            @Override
            public boolean hasNext() {
                return queue != null;
            }

            @Override
            public Item next() {
                if (current == null) {
                    throw new java.util.NoSuchElementException("No more elements to get");
                }
                Item data = current.data;
                current = current.backward;
                return data;
            }

            @Override
            public void remove() {
                throw new java.lang.UnsupportedOperationException("Removal not supported by iterator");
            }
        };
    }


    private void throwIfNull(Item item) {
        if (item == null) {
            throw new java.lang.IllegalArgumentException("Null value not allowed");
        }
    }

    private Item remove(boolean isForward) {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException("Queue is Empty..Don't fetch from empty queue");
        }
        Item data = null;
        count -= 1;
        return data;
    }

    private void add(boolean isForward, Item item) {
        Node node = new Node(item);
        if (node == null) {
            node = new Node(item);
        }
        count += 1;

        if (queue == null) {
            return;
        }

        if (isForward) {
            queue.forward = node;
        } else {
            queue.backward = node;
            node.forward = queue.forward;
        }
    }

    static class Node<Item> {
        private Item data;
        private Node<Item> forward;
        private Node<Item> backward;

        public Node(Item data) {
            this.data = data;
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<String> deck = new Deque<>();
        deck.add(true, "F1");
        deck.add(true, "F2");
        deck.add(true, "F3");
        deck.add(true, "F4");
        System.out.println(deck.removeFirst());
        System.out.println(deck.removeFirst());
        System.out.println(deck.removeFirst());
        System.out.println(deck.removeFirst());
        System.out.println(deck.removeFirst());

    }

}
