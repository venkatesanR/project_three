package com.techmania.designprinciples.creational.factory.mazemanager.maze;

public class Door implements MapSite {
    private Room room1;
    private Room room2;
    private boolean isOpen;

    public Door(Room room1, Room room2) {
        this.room1 = room1;
        this.room2 = room2;
    }

    public Room otherSideFrom(Room current) {
        return null;
    }

    @Override
    public void enter() {

    }
}
