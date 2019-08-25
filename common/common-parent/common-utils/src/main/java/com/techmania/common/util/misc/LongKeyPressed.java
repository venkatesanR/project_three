package com.techmania.common.util.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LongKeyPressed {
    public static char slowestKey(List<List<Integer>> keyTimes) {
        // Write your code here
        int longPressedCharIndex = -1;
        long lastTimeDiff = 0;
        for (List<Integer> keyTimePair : keyTimes) {
            if (keyTimePair.get(1) - lastTimeDiff >= lastTimeDiff) {
                longPressedCharIndex = keyTimePair.get(0);
            }
            lastTimeDiff = keyTimePair.get(1) - lastTimeDiff;
        }
        //There is bug in HC so iterating last
        List<Integer> keyTimePair = keyTimes.get(keyTimes.size() - 1);
        if (keyTimePair.get(1) - lastTimeDiff >= lastTimeDiff) {
            longPressedCharIndex = keyTimePair.get(0);
        }
        return getChar(longPressedCharIndex);
    }

    /***
     * As per ASCII table defination form lang
     * library its derived
     * @param i
     * @return
     */
    private static char getChar(int charIndex) {
        return charIndex >= 0 && charIndex < 26 ? (char) (charIndex + 97) : 'a';
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int keyTimesRows = Integer.parseInt(bufferedReader.readLine().trim());
        int keyTimesColumns = Integer.parseInt(bufferedReader.readLine().trim());

        List<List<Integer>> keyTimes = new ArrayList<>();

        IntStream.range(0, keyTimesRows).forEach(i -> {
            try {
                keyTimes.add(
                        Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                                .map(Integer::parseInt)
                                .collect(Collectors.toList())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        System.out.println(slowestKey(keyTimes));

        bufferedReader.close();
    }
}
