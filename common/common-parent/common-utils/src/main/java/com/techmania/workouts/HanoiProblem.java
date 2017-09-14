package com.techmania.workouts;

/**
 * tower of hanoi problem based on following rules
 * #1.Move one disk at a time
 * #2.Consider given problem consist of tower A,B,C
 * #3.Move N-1 Disk to auxilary tower to make process easier.
 * #4.Move Nth disk to Destination disk and move all auxilary disk to Detstination
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

    public  void towerOfHanoi(int n,char source,char destination,char auxilary) {
        if(n==1) {
            System.out.println("Moving Disk From "+source+" to "+destination);
            return;
        }
        //Move to auxilury disk
        towerOfHanoi(n-1,source,auxilary,destination);

        System.out.println("Moving Disk From "+source+" to "+destination);

        towerOfHanoi(n-1,auxilary,destination,source);

    }
}