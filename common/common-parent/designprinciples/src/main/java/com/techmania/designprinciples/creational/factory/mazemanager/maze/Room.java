package com.techmania.designprinciples.creational.factory.mazemanager.maze;

import java.util.HashMap;
import java.util.Map;

public class Room implements MapSite {
    private int roomNumber;
    private Map<Direction, MapSite> sides = new HashMap<>(4);

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public MapSite getSide(Direction direction) {
        return sides.get(direction);
    }

    public void setSide(Direction direction, MapSite mapSite) {
        sides.put(direction, mapSite);
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    @Override
    public void enter() {

    }
}
