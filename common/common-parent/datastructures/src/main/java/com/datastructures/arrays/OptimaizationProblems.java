package com.datastructures.arrays;

import com.techmania.common.util.file.FileUtl;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class OptimaizationProblems {
    public static void main(String[] args) throws IOException {
        int[] a = {2, 4, 5, 6, 79, 11, 12};
        // a = new int[] { 1, 2, 3, 4, 5 };
        String[] input = FileUtl.readFile("/home/iris/Desktop/IRIS/projects/test_files/algorithms_input/transmitor.txt")
                .split(" ");
        a = new int[input.length];
        int i = 0;
        for (String data : input) {
            a[i] = Integer.valueOf(data);
            i += 1;
        }
        long current = System.currentTimeMillis();
        System.out.println(transmitterProblem(a, 80));
        System.out.println((System.currentTimeMillis() - current));
    }


    /**
     * This approach is a brute force approach to predict
     *
     * @param a
     * @param t
     * @return
     */
    static int transmitterProblem(int[] a, int t) {

        Arrays.sort(a);
        int total = 0;
        int i = 0;
        int leftsum = 0;
        int rightsum = 0;
        int lastAntena = 0;
        while (i < a.length - 1) {
            leftsum += (a[i + 1] - a[i]);
            if (leftsum > t) {
                leftsum -= (a[i + 1] - a[i]);
                int j = i;
                lastAntena = i;
                while (j < a.length - 1) {
                    rightsum += (a[j + 1] - a[j]);
                    if (rightsum > t) {
                        rightsum -= (a[j + 1] - a[j]);
                        i = j;
                        break;
                    } else {
                        j++;
                    }
                }
                if ((leftsum + rightsum) / 2 <= t) {
                    total += 1;
                }
                leftsum = 0;
                rightsum = 0;
                i += 1;
            } else {
                i += 1;
                continue;
            }
        }
        if ((a[a.length - 1] - a[lastAntena] > t) || (total == 0 && leftsum >= 0)) {
            total += 1;
        }
        return total;
    }

    static double solveSingleVariableEqn(String equation, String variable) {
        String[] sign = equation.split("[0-9," + variable + ",=]");
        String[] variables = equation.split("[-?+=]");
        double varCoEff = 0;
        double constant = 0;
        boolean foundEqual = false;
        for (int index = 0; index < variables.length; index++) {
            String appender = "";
            if (sign[index] != null) {
                if (!foundEqual && (sign[index].trim() == "+" || sign[index].trim() == "")) {
                    appender = "+";
                } else if (!foundEqual && (sign[index].trim() == "-")) {
                    appender = "-";
                }
                if (foundEqual && (sign[index].trim() == "+" || sign[index].trim() == "")) {
                    appender = "-";
                } else if (foundEqual && (sign[index].trim() == "-")) {
                    appender = "+";
                } else {
                    foundEqual = true;
                    continue;
                }
                if (variables[index].contains(variable)) {
                    varCoEff = varCoEff + Double.valueOf(appender.concat(variable.replace(variable, "")));
                } else {
                    constant = constant + Double.valueOf(appender.concat(variable));
                }
            }

        }
        return (varCoEff > 0 ? (-constant / varCoEff) : 0.0);
    }

    static String isBalanced(String s) {
        // Complete this function
        if (s != null && !s.trim().equals("")) {
            String matched = "YES";
            if (s.length() % 2 != 0) {
                matched = "NO";
                return matched;
            }
            List<String> left = Arrays.asList(new String[]{"{", "[", "("});
            List<String> right = Arrays.asList(new String[]{"}", "]", ")"});
            Stack<String> ls = new Stack<String>();
            for (char c : s.toCharArray()) {
                String modC = String.valueOf(c);
                if (left.contains(modC)) {
                    ls.push(modC);
                } else if (right.contains(modC)) {
                    if (!ls.isEmpty()) {
                        String modl = ls.pop();
                        if (left.indexOf(modl) != right.indexOf(modC)) {
                            matched = "NO";
                            break;
                        }
                    } else {
                        matched = "NO";
                        break;
                    }
                }
            }
            if (!ls.isEmpty()) {
                matched = "NO";
            }
            return matched;

        } else {
            return "";
        }
    }

}
