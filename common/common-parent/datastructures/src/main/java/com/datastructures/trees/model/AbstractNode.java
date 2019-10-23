package com.datastructures.trees.model;

public class AbstractNode<T> {
    private T data;
    private Boolean visited;
    private Boolean isRoot;

    public AbstractNode(T data) {
        this.data = data;
    }

    public AbstractNode(T data, Boolean isRoot) {
        this(data);
        this.isRoot = isRoot;
    }

    public T getData() {
        return data;
    }

    public Boolean isVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }
}