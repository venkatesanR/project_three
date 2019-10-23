package com.techmania.designprinciples.creational;

import com.techmania.designprinciples.creational.factory.mazemanager.maze.Room;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <table border="1">
 * <tr>
 * <td>Creational Patterns</td>
 * <td>Abstract Factory</td>
 * <td>Builder</td>
 * <td>Factory Method Prototype</td>
 * <td>Singleton</td>
 * </tr>
 * </table>
 *
 * @author vrengasamy
 */
public class DriverClass {
    private static final Map<String, CompletableFuture<Room>> futures = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            CompletableFuture data = new CompletableFuture<Room>();
            futures.put("A", data);
            futures.get("A").complete(new Room(1));
            System.out.println(futures.get("A").get().getRoomNumber());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(futures.size());
    }
}
