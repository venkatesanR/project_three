package com.designpatterns.structural;

public class NokiaChargerImpl implements NokiaCharger {
	private int voltage;

	public NokiaChargerImpl(int volts) {
		this.voltage = volts;
	}

	@Override
	public void stopCharge() {
		this.voltage = 0;
	}

	@Override
	public boolean isCharging() {
		return this.voltage > 0;
	}

	@Override
	public int getVoltage() {
		return this.voltage;
	}
}
