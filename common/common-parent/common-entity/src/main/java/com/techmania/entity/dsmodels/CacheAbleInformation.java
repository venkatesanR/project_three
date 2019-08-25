package com.techmania.entity.dsmodels;

public class CacheAbleInformation {
    final String id;
    final String subMatch;
    final Long timestamp;
    String craneId;

    public CacheAbleInformation(String id, String subMatch, long timestamp) {
        this.id = id;
        this.subMatch = subMatch;
        this.timestamp = timestamp;
    }

    public void setCraneId(String craneId) {
        this.craneId = craneId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return id + " : " + timestamp;
    }
}