package com.techmania.datastreams;

public class SimplLabel {
    public static void main(String[] args) throws InterruptedException {
        SimplLabel l1 = new SimplLabel();
        l1.executeMyLabel();
    }

    private void executeMyLabel() throws InterruptedException {
        int count = 0;
        labelRef:
        while (true) {
            System.out.print(count);
            while (true) {
                Thread.sleep(1000);
                count += 1;
                if (count < 10) {
                    continue labelRef;
                } else {
                    break labelRef;
                }
            }
        }
    }
}
