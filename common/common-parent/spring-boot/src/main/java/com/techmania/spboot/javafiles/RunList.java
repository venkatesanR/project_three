package com.techmania.spboot.javafiles;

public class RunList {

    public static void main(String args[]) {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);
        list.add(8);
        list.add(9);
        list.add(10);
        list.add(11);
        list.add(12);
        list.add(13);
        list.add(14);
        list.add(15);
        list.add(16);
        list.add(17);
        list.add(18);
        list.add(19);
        list.add(20);

        System.out.println("GetValue:" + list.get(2));

        System.out.println("Disply List:" + list.values());
        list.remove(8);
        list.remove(7);
        list.remove(6);

        System.out.println("Disply List:" + list.values());
    }

}
