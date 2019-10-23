package com.techmania.designprinciples.behaviour;

import java.util.Scanner;

/**
 * <table border="1">
 * <tr>
 * <td>Behavioral Patterns</td>
 * <td>Chain of Responsibility</td>
 * <td>Command</td>
 * <td>Interpreter</td>
 * <td>Iterator</td>
 * <td>Mediator</td>
 * <td>Memento</td>
 * <td>Observer</td>
 * <td>State</td>
 * <td>Strategy</td>
 * <td>Template Method</td>
 * <td>Visitor</td>
 * </tr>
 * </table>
 *
 * @author vrengasamy
 */
public class DriverClass {
    public static void main(String[] args) {
        Scanner scanner = null;
        try {
            scanner = new Scanner(System.in);
            System.out.println(reverseLine(scanner.nextLine(), "."));
        } catch (Exception ex) {
            //NO0OP
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static String reverseLine(String input, String seperator) {
        StringBuilder builder = new StringBuilder();
        String[] words = input.split(" ");
        for (int i = words.length - 1; i >= 0; i--) {
            builder.append(words[i]).append(seperator);
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
}
