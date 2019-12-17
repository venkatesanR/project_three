package com.techmania.designprinciples.structural.adapter;

/**
 * <p>
 * Primary Members of an adapter pattern describes below Client Target Adapter
 * Adaptee
 * 
 * Consider the scenario You are writing some Application Based on Third party
 * API's today you are writing API for Adaptee(i) You have done all
 * implementation based on client 1 tomorrow Client 2 you want to integrate then
 * you should have to re write all code and also it's unmaintable.To Clarify
 * this Make One Common object That will underStand your system and create an
 * adapter to infront of your common code transform all client code with common
 * object and transfrom it..so that you dont have to re wright your code.
 * </p>
 * 
 * @author vrengasamy
 */
public class AdapterPattern {
	public static void main(String[] args) {
		int volts = 230;
		NokiaCharger charger = new NokiaChargerImpl(volts);
		System.out.println("Nokia Mobile IS CHARGING:" + charger.isCharging());
		System.out.println(charger.getVoltage());
		charger.stopCharge();
		System.out.println("Nokia Mobile IS CHARGING:" + charger.isCharging());
		charger = new NokiaChargerImpl(volts);
		IPhoneCharger iCharger = new IPhoneAdapterImpl(charger);
		System.out.println("Iphone IS CHARGING:" + iCharger.isCharging());
		iCharger.stopCharging();
		System.out.println("Iphone IS CHARGING:" + iCharger.isCharging());
	}
}
