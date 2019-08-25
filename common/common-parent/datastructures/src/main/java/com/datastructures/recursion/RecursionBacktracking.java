package com.datastructures.recursion;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

import javax.activity.InvalidActivityException;

public class RecursionBacktracking {
    private static final int[] data = new int[]{0, 1, 2, 3, 4, 5, 6};
    private static final int[] RUNS = new int[]{0, 1, 2, 3, 4, 5, 6};
    private static final int[] PASSWORD = new int[]{0, 1, 2, 3, 4, 5, 6};

    private static int N;
    private static int M;
    private static int max = 1000;

    private RecursionBacktracking() throws InvalidActivityException {
        throw new InvalidActivityException();
    }

    /**
     * Finds factorial of an given number <input> call's recursively until all
     * methods stacks find its own fact number fact(n)={ n*fact(n-1) n > 0; 1 n
     * =0;
     *
     * @param input
     * @return
     */
    public static BigInteger fact(BigInteger input) {
        BigInteger result = new BigInteger("1");
        if (input.equals(BigInteger.ZERO) || input.equals(BigInteger.ONE)) {
            return result;
        } else {
            return input.multiply(fact(input.subtract(BigInteger.ONE)));
        }
    }

    /**
     * This method used to find for an given array is sorted or not By calling
     * recursively by decrement array index and
     *
     * @param a
     * @param n
     * @return
     */
    public static boolean isSorted(int[] a, int n) {
        if (n == 1) {
            return true;
        } else {
            return (a[n - 1] < a[n - 2] ? false : isSorted(a, n - 1));
        }
    }

    /**
     * This util method used to generate binary numbers Eg: given data is 3
     * digit long(with base of 2 Binary) Output should be 000 001 010 011 100
     * 101 110 111 Using backtracking logic we can achieve this output consider
     * size 3 array contains all 3 zero's initial i1: time complexity O(n)=2^n
     *
     * @param length
     */
    public static void generateBinary(int length) {
        if (length < 1) {
            Arrays.toString(data);
        } else {
            // consider input array here is Global variable to fix
            data[length - 1] = 0;
            generateBinary(length - 1);
            data[length - 1] = 1;
            generateBinary(length - 1);
        }
    }

    public static void printFibbonaci(int max) {

    }

    /**
     * Draw K numbers from N Sized array permutations time complexity O(n)=k^n
     *
     * @param n
     * @param k
     */
    public static void drawKNumbers(int n, int k) {
        if (n < 1) {
            System.out.println(Arrays.toString(data));
        } else {
            for (int j = 0; j < k; j++) {
                data[n - 1] = j;
                drawKNumbers(n - 1, k);
            }
        }
    }

    /**
     * Improved Fibbonacci comming under Dynamic Programming
     * <p>
     * F(n) ={ 0 n=-2; 1 n=-1; Tn+Tn+1 Else where n >= 1
     */
    public static BigInteger bigFibbo(int n, int a, int b) {
        BigInteger t1 = new BigInteger(String.valueOf(a));
        BigInteger t2 = new BigInteger(String.valueOf(b));
        BigInteger result = new BigInteger("0");
        int i = 3;
        while (i <= n) {
            result = t1.add(t2.multiply(t2));
            t1 = t2;
            t2 = result;
            i++;
        }
        return result;
    }

    /**
     * Actually Idea here was to implement recursive solution to Identify
     * serious count as mentioned below. N=Actual Number k=power N=a^k+b^k+...
     * prove(if k=2) 10=1^2+3^2=10(count=2)
     *
     * @param result
     * @param power
     * @return
     */

    public static void passwordMathcher(int N, int M, String[] posibility) {
    }

    private static void printAllCombinations(int N, int M, int[] posibility) {
        if (M == 0) {
            int sum = 0;
            for (int l : posibility) {
                sum += l;
            }
            if (max > sum) {
                max = sum;
            }
            System.out.println(Arrays.toString(posibility));
        } else {
            for (int i = 0; i < RUNS.length; i++) {
                posibility[M - 1] = RUNS[i];
                printAllCombinations(N, M - 1, posibility);
            }
        }
    }

    private static final BigInteger TEN = new BigInteger("10");

    static int digitSum(String n, int k) {
        String[] a1 = n.split("");
        int sum = k * recursiveSum(a1, 0, 0);
        while (sum > 10) {
            String[] a2 = String.valueOf(sum).split("");
            sum = recursiveSum(a2, 0, 0);
        }
        return sum;
    }

    static int recursiveSum(String[] input, int sum, int count) {
        if (count == input.length) {
            return sum;
        } else {
            sum = Integer.valueOf(input[count]) + recursiveSum(input, sum, count + 1);
        }
        return sum;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String n = in.next();
        int k = in.nextInt();
        int result = digitSum(n, k);
        System.out.println(result);
        in.close();
    }

}
