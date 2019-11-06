package com.jmodules.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DriverClass {
    private static Pattern pattern = Pattern.compile("([0-9]*)?_([a-zA-Z0-9\\s])+(.xml)$");

    private static boolean isFileNameValid(String filename) {
        return pattern.matcher(filename).matches();
    }

    public static void main(String[] args) {
        System.out.println(isFileNameValid("20190911091511000_20190911091545fileName.xml"));
        System.out.println(isFileNameValid("XYS_20190911091545fileName.xml"));
    }

    private Map<String, Long> wordCount(String fileName) throws IOException {
        List<String> lines = FileUtils.readLines(new File(fileName));
        Map<String, Long> WORD_COUNT_MAP = new HashMap<>();
        lines.stream().
                map(word -> word.split(" ")).
                map(words -> Arrays.asList(words))
                .reduce(null, null);


        lines.stream().forEach(line -> {
            if (WORD_COUNT_MAP.containsKey(line)) {
                WORD_COUNT_MAP.put(line, WORD_COUNT_MAP.get(line) + 1);
            } else {
                WORD_COUNT_MAP.put(line, 1l);
            }
        });
        return WORD_COUNT_MAP;
    }
}
