package com.techmania.workouts;

import java.math.BigInteger;

public class FunctionUtils {
    private FunctionUtils() {

    }

    public static void main(String[] args) {
        int[] a={1,2,3,4,6,8,3};
        System.out.println(isSorted(a,a.length));
    }

    /**
     * Finds factorial of an given number <input>
     *     call's recursively until all methods stacks find its own fact number
     *     fact(n)=n*fact(n-1) > 0;
     *     1                   =0;
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
     * This method used to find for an given array is sorted or not
     * By calling recursively by decrement array index and
     * @param a
     * @param n
     * @return
     */
    public static boolean isSorted(int[] a,int n) {
        if(n==1) {
            return true;
        } else {
            return (a[n-1] < a[n-2]?false:isSorted(a,n-1));
        }
    }
}

