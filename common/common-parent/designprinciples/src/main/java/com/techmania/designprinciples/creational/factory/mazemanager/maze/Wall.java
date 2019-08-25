package com.techmania.designprinciples.creational.factory.mazemanager.maze;

public class Wall implements MapSite {
    @Override
    public void enter() {
        System.out.println("Opps!!! Dont break your nose");
    }
}
