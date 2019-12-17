package com.techmania.designprinciples.structural.adapter;

public interface NokiaCharger {
	public void stopCharge();
	public boolean isCharging();
	public int getVoltage();
}
