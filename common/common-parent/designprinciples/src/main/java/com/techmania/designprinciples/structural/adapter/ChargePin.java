package com.techmania.designprinciples.structural.adapter;

public class ChargePin {
    private int voltage;

    public ChargePin(int voltage) {
        this.voltage = voltage;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }
}
