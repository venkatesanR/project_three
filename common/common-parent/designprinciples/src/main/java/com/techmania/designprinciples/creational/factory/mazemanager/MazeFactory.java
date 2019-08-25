package com.techmania.designprinciples.creational.factory.mazemanager;

import com.techmania.designprinciples.creational.factory.mazemanager.maze.Door;
import com.techmania.designprinciples.creational.factory.mazemanager.maze.Maze;
import com.techmania.designprinciples.creational.factory.mazemanager.maze.Room;
import com.techmania.designprinciples.creational.factory.mazemanager.maze.Wall;

public class MazeFactory {
    public Maze createMaze() {
        return new Maze();
    }

    public Room createRoom(Integer roomNumber) {
        return new Room(roomNumber);
    }

    public Wall createWall() {
        return new Wall();
    }

    public Door createDoor(Room roomOne, Room roomTwo) {
        return new Door(roomOne, roomTwo);
    }
}
