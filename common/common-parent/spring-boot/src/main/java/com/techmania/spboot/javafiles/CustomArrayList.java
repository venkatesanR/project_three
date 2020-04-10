package com.techmania.spboot.javafiles;

import java.util.Arrays;

public class CustomArrayList<T> implements CustomList<T> {

    private static final int DEFAULT_SIZE = 10;
    private static final float LOAD_FACTOR = 0.75f;
    private int index = 0;
    private T[] bucket = null;


    public CustomArrayList() {
        this(DEFAULT_SIZE);
    }

    public CustomArrayList(int capacity) {
        bucket = (T[]) new Object[capacity];
    }


    @Override
    public void add(T element) {
        updateBucket(true);
        bucket[index] = element;
        index += 1;
    }

    @Override
    public T get(int index) {
        if (index >= this.bucket.length) throw new RuntimeException("Index out of bound");
        return bucket[index];
    }

    @Override
    public String values() {
        return Arrays.toString(bucket);
    }

    @Override
    public void remove(int removeIndex) {
        for (int i = removeIndex; i < index - 1; i++) {
            bucket[i] = bucket[i + 1];
        }
        bucket[index - 1] = null;
        index -= 1;
        updateBucket(false);
    }

    private void updateBucket(boolean increase) {
        float marker = (float) index / bucket.length;
        int update = 0;

        if (increase && marker >= LOAD_FACTOR) {
            update = 1;
        } else if (!increase && marker <= (1 - LOAD_FACTOR)) {
            update = -1;
        }
        if (update != 0) {
            update *= (int) (LOAD_FACTOR * bucket.length);
            T[] updated = (T[]) new Object[bucket.length + update];
            System.arraycopy(bucket, 0, updated, 0, index);
            this.bucket = updated;
        }
    }
}
