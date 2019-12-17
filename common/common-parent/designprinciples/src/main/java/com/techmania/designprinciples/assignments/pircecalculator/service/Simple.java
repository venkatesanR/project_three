package com.techmania.designprinciples.assignments.pircecalculator.service;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Simple {
    public static void main(String[] args) {
        String[] input = {"a", "b", "c"};
        System.out.println(Arrays.toString(solution(input, 1l)));
    }

    public static String[] solution(String[] words, long width) {
        String response = Stream.of(words).collect(Collectors.joining(" ")).concat("  ");
        int arraySize = response.length() / (int) width;
        String[] data = new String[arraySize];
        int start = 0;
        int end = (int) width;
        for (int i = 0; i < arraySize; i++) {
            if (end >= response.length()) {
                end = response.length();
            }
            data[0] = response.substring(start, end).concat(",");
            start = end;
            end = end + (int) width;
        }
        return data;
    }

    private static String longestPrefix(String[] strings) {
        String prefix = "";
        if (strings == null || strings.length == 0) {
            return prefix;
        }
        if (strings.length == 1) {
            return strings[0];
        }
        Arrays.sort(strings);

        int minlength = Math.min(strings[0].length(), strings[strings.length - 1].length());

        int i = 0;
        while (i < minlength && strings[0].charAt(i) == strings[strings.length - 1].charAt(i))
            i++;
        prefix = strings[0].substring(0, i);
        return prefix;
    }
}
