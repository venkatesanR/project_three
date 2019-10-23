package com.techmania.datastreams;

public class SimplLabel {
    public static void main(String[] args) throws InterruptedException {
        SimplLabel l1 = new SimplLabel();
        l1.executeMyLabel();
    }

    private void executeMyLabel() throws InterruptedException {
        labelRef:
        while (true) {
            System.out.println("One");
            while (true) {
                //break;
                System.out.println("Two");
                Thread.sleep(1000);
                continue labelRef;
            }
        }
    }
}
