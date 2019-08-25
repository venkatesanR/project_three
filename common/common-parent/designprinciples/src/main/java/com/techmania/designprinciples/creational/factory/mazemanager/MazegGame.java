package com.techmania.designprinciples.creational.factory.mazemanager;

import com.techmania.designprinciples.creational.factory.mazemanager.maze.Direction;
import com.techmania.designprinciples.creational.factory.mazemanager.maze.Maze;
import com.techmania.designprinciples.creational.factory.mazemanager.maze.Room;

public class MazegGame {
    private MazeFactory mazeFactory;

    public MazegGame() {
        mazeFactory = new MazeFactory();
    }

    public Maze createMaze() {
        Maze maze = mazeFactory.createMaze();
        Room room1 = mazeFactory.createRoom(1);
        Room room2 = mazeFactory.createRoom(2);

        room1.setSide(Direction.NORTH, mazeFactory.createWall());
        room1.setSide(Direction.EAST, mazeFactory.createDoor(room1, room2));
        room1.setSide(Direction.SOUTH, mazeFactory.createWall());
        room1.setSide(Direction.WEST, mazeFactory.createWall());

        room2.setSide(Direction.NORTH, mazeFactory.createWall());
        room2.setSide(Direction.EAST, mazeFactory.createWall());
        room2.setSide(Direction.SOUTH, mazeFactory.createWall());
        room2.setSide(Direction.WEST, mazeFactory.createDoor(room1, room2));

        maze.addRoom(room1);
        maze.addRoom(room2);
        return maze;
    }

    public static void main(String[] args) {
        MazegGame game = new MazegGame();
        Maze maze = game.createMaze();
        System.out.println(maze.getRoom(1));
    }
}
