package com.techland.training.venkat;

import java.io.PrintStream;
import java.util.Random;

public class StdOut {
    public final static PrintStream out = System.out;
    public static Random rn = new Random();

    private void StdOut() {

    }

    public static void print(String data) {
        out.print(data);
    }

    public static void println(String data) {
        out.println(data);
    }
    public static Integer uniform(int min, int max) {
        int range = max - min + 1;
        return rn.nextInt(range) + min;
    }
}
