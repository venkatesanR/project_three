package com.techmania.designprinciples.creational.factory.mazemanager.maze;

import java.util.HashMap;
import java.util.Map;

public class Maze {
    public Map<Integer, Room> roomMap = new HashMap<>();


    public void addRoom(Room room) {
        roomMap.put(room.getRoomNumber(), room);
    }

    public Room getRoom(Integer roomNumber) {
        return roomMap.get(roomNumber);
    }
}