package com.techmania.recusrion;

/**
 * tower of hanoi problem based on following rules
 * #1.Move one disk at a time
 * #2.Consider given problem consist of tower A,B,C
 * #3.Move N-1 Disk to auxiliary tower to make process easier.
 * #4.Move Nth disk to Destination disk and move all auxiliary disk to Destination
 * T
 */
public class HanoiProblem {
    private int totalDisk;
    public HanoiProblem(int totalDisk) {
        this.totalDisk=totalDisk;
    }

    public static void main(String[] args) {
        char towerA='A';
        char towerB='B';
        char towerC='C';
        HanoiProblem hanoi=new HanoiProblem(3);
        hanoi.towerOfHanoi(hanoi.totalDisk,towerA,towerC,towerB);
    }

    public  void towerOfHanoi(int n,char source,char destination,char auxiliary) {
        if(n==1) {
            System.out.println("Moving Disk From "+source+" to "+destination);
            return;
        }
        //Move to auxiliary disk
        towerOfHanoi(n-1,source,auxiliary,destination);

        System.out.println("Moving Disk From "+source+" to "+destination);

        towerOfHanoi(n-1,auxiliary,destination,source);

    }
}