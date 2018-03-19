package com.designpatterns.structural;

public class IPhoneAdapterImpl implements IPhoneCharger {
	private final ChargePin iphoneCharger;

	public IPhoneAdapterImpl(NokiaCharger nokiaCharger) {
		this.iphoneCharger = new ChargePin(nokiaCharger.getVoltage());
	}

	@Override
	public String isCharging() {
		return this.iphoneCharger.getVoltage() > 0 ? "YES" : "NO";
	}

	@Override
	public void stopCharging() {
		this.iphoneCharger.setVoltage(0);
	}
}
