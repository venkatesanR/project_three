package com.techmania.common.util.misc;

public class KeyValue<Key, Value> {
    private Key key;
    private Value value;

    public KeyValue(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    public Key getKey() {
        return key;
    }

    public Value getValue() {
        return value;
    }
}
